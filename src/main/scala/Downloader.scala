import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

import java.io.{File, PrintWriter}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import spray.json.DefaultJsonProtocol
import com.typesafe.config._

import scala.util.{Success, Try}

case class Post(body: String, id: Int, title: String, userId: Int) {
  def savePost(path: String): Unit = {
    import PostJsonProtocol._
    val savePath = path ++ "/" ++ this.id.toString ++ ".json"
    val w = new PrintWriter(new File(savePath))
    println("Saving file: " ++ savePath)
    w.write(this.asInstanceOf[Post].toJson.prettyPrint)
    w.close()
  }
}

object PostJsonProtocol extends DefaultJsonProtocol {
  implicit val postFormat: RootJsonFormat[Post] = jsonFormat4(Post.apply)
}

object Downloader extends App {
  import PostJsonProtocol._
  import spray.json._

  implicit val sys: ActorSystem = ActorSystem("post-download")

  val conf = ConfigFactory.parseFile(new File("resources/application.conf"))
  val directory = new File(conf.getString("download-folder"))
  if (!directory.exists) directory.mkdir

  val responseFuture =
    Http().singleRequest(HttpRequest(uri = conf.getString("download-uri")))

  responseFuture.flatMap { r: HttpResponse => Unmarshal(r).to[String] }
    .map(_.parseJson)
    .map(_.toJson)
    .map(_.convertTo[List[JsObject]])
    .map(_.map(_.convertTo[Post]))
    .onComplete((futComp: Try[List[Post]]) => futComp match {
      case Success(listOfPosts) => listOfPosts.foreach(_.savePost(conf.getString("download-folder")))
    })
}
