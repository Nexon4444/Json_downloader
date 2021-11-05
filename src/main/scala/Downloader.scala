
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json.JsValue
import spray.json._

import java.io.{File, PrintWriter}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success, Try}

case class Post(body: String, id: Int, title: String, userId: Int)  {
  def savePost(path: String): Unit = {
    val savePath = path ++ "/" ++ this.id.toString ++ ".json"
    import PostJsonProtocol._
    val w = new PrintWriter(new File(savePath))
    println("Saving file: " ++ savePath)
    w.write(this.asInstanceOf[Post].toJson.prettyPrint)
    w.close()
  }
}

object PostJsonProtocol extends DefaultJsonProtocol {
  implicit val postFormat: RootJsonFormat[Post] = jsonFormat4(Post.apply)
}


object Downloader {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("test")
    implicit val sys: ActorSystem = system
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = "https://jsonplaceholder.typicode.com/posts"))

    val jsonStringFuture = responseFuture.flatMap{ r: HttpResponse => Unmarshal(r).to[String]}
    val jsonValFut: Future[JsValue] = jsonStringFuture.map(_.parseJson)

    import spray.json._
//    val jsonToParse = jsonValFut.map(_.convertTo[List[JsObject]])
    val jsonVal: JsValue = Await.result(jsonValFut, 5.seconds)
    println(jsonVal.prettyPrint)
    import PostJsonProtocol._

    val listOfPosts =
    Try {
      val json = jsonVal.toJson
      val listOfJsObjects = json.convertTo[List[JsObject]]
      listOfJsObjects.map(_.convertTo[Post])
    } match {
      case Failure(e: DeserializationException) => throw e
//      case Failure(e: DeserializationException) => throw e
      case Success(value) => value
    }
    listOfPosts.foreach(_.savePost("./resources"))
    }
}
