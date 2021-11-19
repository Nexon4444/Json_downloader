import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

import java.io.{File, PrintWriter}
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import spray.json.DefaultJsonProtocol
import com.typesafe.config._

import scala.concurrent.Future
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

case class Comment(postId: Int, id: Int, name: String, email: String, body: String, title: String, userId: Int) {
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

  val conf: Config = ConfigFactory.parseFile(new File("resources/application.conf"))
  val directory = new File(conf.getString("download-folder"))
  if (!directory.exists) directory.mkdir

  getAndSavePosts(conf)

  def getAndSavePosts(conf: Config)= {
    for {
      r <- response(conf.getString("download-uri"))
      posts <- convertPostsFromResponse(r)
      _ = savePosts(posts, conf.getString("download-folder"))
    } yield ()
  }

//  def response(uri: Uri) = Http().singleRequest(HttpRequest(uri = uri))

  def convertPostsFromResponse(resp: HttpResponse) = {
    val r = Unmarshal(resp).to[String]
    r.map(_.parseJson.convertTo[List[Post]])
  }

  def savePosts(posts: List[Post], path: String) = {
    posts.foreach(_.savePost(path))
  }
//  Future[Option[Future[X]]] -> Future[Future[Option[X]]] -> Future[Option[X]]
  def getCommentsFromPosts(posts: List[Post], n: Int) = {
    val x = posts.take(n).map(post => getCommentsForId(post.id).flatMap(convertCommentFromResponse))
    Future.sequence()
  }

  def getCommentsForId(id: Int, uri: Uri) = {
    response(uri.toString() + id.toString)

  }

  def convertCommentFromResponse(resp: HttpResponse) = {
    val r = Unmarshal(resp).to[String]
    r.map(_.parseJson.convertTo[List[Comment]])
  }
}
///comments?postId=1