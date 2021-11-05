
import akka.actor.ActorSystem
import akka.actor.TypedActor.{context, self}
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.omg.PortableServer.POA
import spray.json.DefaultJsonProtocol.{JsValueFormat, RootJsObjectFormat, StringJsonFormat, jsonFormat4, listFormat}
import spray.json.JsValue
import ujson.Value.Value
import spray.json._

import java.io.{BufferedWriter, FileWriter}
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import scala.concurrent.Future.never
import scala.concurrent.Future.never.{onComplete, value}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.parsing.json.JSONObject
import scala.util.{Failure, Success, Try}
//import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


//import scala.sys.process.processInternal.File
//import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
//import spray.json.DefaultJsonProtocol
//import akka.actor.ActorSystem
case class Post(val body: String, val id: Int, val title: String, val userId: Int) extends DefaultJsonProtocol {
  implicit val postFormat: RootJsonFormat[Post.this.type] = jsonFormat4(Post.apply)
  Post.type


  def savePost(path: Path): Unit = {
//    implicit val postFormat: RootJsonFormat[Post.this.type] = jsonFormat4(Post.apply)
    //    import spray.json._
//    import PostJsonProtocol._
    val w = new BufferedWriter(new FileWriter(path.toString ++ this.id.toString ++ ".json"))
    w.write(this.toJson.prettyPrint)
  }
}

object Post {
  def savePost(post: Post, path: Path): Unit = {
//    import spray.json._
    import spray.json._
    implicit val postFormat: RootJsonFormat[Post] = jsonFormat4(Post.apply)
    import PostJsonProtocol._
    val w = new BufferedWriter(new FileWriter(path.toString ++ post.id.toString ++ ".json"))
    w.write(post.toJson.prettyPrint)
  }
}

object PostJsonProtocol extends DefaultJsonProtocol {
  type AllPost >: Post
  implicit val postFormat: RootJsonFormat[AllPost] = jsonFormat4(Post.apply)

//  def write(p: Post): JsObject =
//    JsObject("body" -> JsString(p.body), "id" -> JsNumber(p.id), "title" -> JsString(p.title), "userId" -> JsNumber(p.userId))


}



//case class Color(name: String, red: Int, green: Int, blue: Int) {
//  def save = {
//    import MyJsonProtocol._
//    implicit val colorFormat = jsonFormat4(Color.this.type)
//    val color = this.toJson
//    val json = Color("CadetBlue", 95, 158, 160).toJson
//  }
//}
//
//object MyJsonProtocol extends DefaultJsonProtocol {
//  implicit val colorFormat = jsonFormat4(Color)
//}
//
//object

class Downloader  {

//  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
//  implicit val materializer = typedActorSystem.classicSystem

}

object Downloader {
//  def writeToFile(name: String)
  def main(args: Array[String]): Unit = {
//    va
    val system: ActorSystem = ActorSystem("test")
    implicit val sys: ActorSystem = system
    //  implicit ClassicActorSystemProvider
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = "https://jsonplaceholder.typicode.com/posts"))


//    responseFuture.map((v: HttpResponse) => println(Unmarshal(_).to[String]))
//    val resp: HttpResponse = Await.result(responseFuture., Duration(500, TimeUnit.SECONDS))
//    wait(2)
//    val jsonString: Future[List[String]] = responseFuture.flatMap{ r: HttpResponse => Unmarshal(r).to[List[String]]
    val jsonStringFuture = responseFuture.flatMap{ r: HttpResponse => Unmarshal(r).to[String]}
    val jsonValFut: Future[JsValue] = jsonStringFuture.map(_.parseJson)

    val jsonVal: JsValue = Await.result(jsonValFut, 5.seconds)
    println(jsonVal.prettyPrint)
    import Post.postF
    val x = jsonVal.toJson
    val z = x.convertTo[List[JsObject]]
    val zz: List[Post] = z.map(_.convertTo[Post](PostJsonProtocol.postFormat))
    zz(0).toJson
    val f: JsObject = z.toList.head
    val y = jsonVal.toJson.convertTo[List[String]]
    val w = new BufferedWriter(new FileWriter("output.txt"))
    w.write(f.toJson.prettyPrint)
    println(jsonVal.toJson.convertTo[List[String]])

//    jsonString.onComplete{x: Try[String] => x parseJson}
//    implicit val jsonWriter = JsonWriter
//    val json = jsonVal.flatMap{
//      case Failure(el) => el
//      case Success(el) => el}

//    { fut: Try[JsValue] =>
//      fut match {
//        case Failure(exception) =>
//        case Success(value) => value
//      } }
//      r match {
////        case Failure(exception) => None
//        case Success(value) => Unmarshal(value).to[String]
//      }

//    import MyJsonProtocol._
//    import spray.json._
//
//    val json = Color("CadetBlue", 95, 158, 160).toJson
//    val color = json.convertTo[Color]
    }

//    println(responseFuture.isCompleted)
//    if (resp.entity.contentType == ContentTypes.`application/json`) {
//      println(Unmarshal(resp).to[String])
//    }
//    val x=3
//  }

//  def downloadViaFutures(uri: Uri, file: File): Future[Long] = {
//    val system: ActorSystem = ActorSystem("test")
//    implicit val sys: ActorSystem = system
//    val request = Get(uri)
//    val responseFuture = Http().singleRequest(request)
//    responseFuture.flatMap { response =>
//      val source = response.entity.dataBytes
//      source.runWith(FileIO.toFile(file))
//    }
//  }

}
