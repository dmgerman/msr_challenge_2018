import scala.io.StdIn._
import scala.io._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import java.io.{ IOException, FileOutputStream, FileInputStream, File }
import java.util.zip.{ ZipEntry, ZipInputStream }
import collection.JavaConverters._

import slick.driver.SQLiteDriver.api._
import slick.jdbc.SQLiteProfile.api._
import org.sqlite.SQLiteConfig

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.io.Source



object Main {




  sealed trait CommonFields {
    val typeid: String
    val idesessionuuid: String
    val kaveversion: String
    val triggeredat: String
    val triggeredby: Int
    val duration: Option[String]
    val activewindow: String
    val activedocument: Option[String]
  }

  case class EditClass(
    typeid: String,
    // TODO
    //    context2: String,
    numberChanges: Int,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String]
  )

  case class SystemClass(
    typeid: String,
    typeval : Int,
    // TODO
    //    context2: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class FindClass(
    typeid: String,
    cancelled: Boolean,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )


  case class WindowClass(
    typeid: String,
    window: String,
    action : Int,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: String,
    activedocument: Option[String]
  )

  case class CommandsClass(
    typeid: String,
    commandid: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class ActivityClass(
    typeid: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: String,
    activedocument: Option[String]
  )


  case class NavClass(
    typeid: String,
    target: String,
    location: String,
    navType: Int,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: String,
    activedocument: Option[String]
  )

  case class DebuggerClass(
    typeid: String,
    mode: Int,
    reason: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class SolutionClass(
    typeid: String,
    action: Int,
    target: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: String,
    activedocument: Option[String]
  )

  case class DocumentClass(
    typeid: String,
    document: Option[String],
    action: Int,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: String,
    activedocument: Option[String]
  )

  case class UserClass(
    profileid: String,
    education: Int,
    position: Int,
    projectsCourses: Boolean,
    projectsPersonal : Boolean,
    projectsSharedSmall : Boolean,
    projectsSharedMedium : Boolean,
    projectsSharedLarge : Boolean,
    teamsSolo : Boolean,
    teamsSmall : Boolean,
    teamsMedium : Boolean,
    teamsLarge : Boolean,
    codeReviews : Int,
    programmingGeneral : Int,
    programmingCSharp : Int,
    comment : String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class TargetClass (
    targettype : String,
    project: String,
    projectConfiguration: String,
    platform: String,
    solutionConfiguration: String,
    startedAt: String,
    duration: String,
    successful: Boolean
  )

  case class TargetClassDB (
    idesessionuuid: String,
    triggeredat: String,
    targettype : String,
    project: String,
    projectConfiguration: String,
    platform: String,
    solutionConfiguration: String,
    startedAt: String,
    duration: String,
    successful: Boolean
  )

  case class BuildClass (
    typeid: String,
    scope: String,
    action: String,
    targets: Seq[TargetClass],
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class BuildClassDB (
    typeid: String,
    scope: String,
    action: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class TestCaseClass(
    testcasetype: String,
    testmethod: String,
    parameters: String,
    duration: String,
    result: Int
  )

  case class TestCaseClassDB(
    idesessionuuid: String,
    triggeredat: String,
    testcasetype: String,
    testmethod: String,
    parameters: String,
    duration: String,
    result: Int
  )

  case class TestClass(
    typeid: String,
    wasaborted: Boolean,
    testcases: Seq[TestCaseClass],
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]

  )

  case class TestClassDB(
    typeid: String,
    wasaborted: Boolean,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )


  case class IDEStateClassDB(
    typeid: String,
    ideLifeCyclePhase: Int,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class IDEStateAttDB(
    idesessionuuid: String,
    triggeredat: String,
    att        : String,
  )

  case class IDEStateClass(
    typeid: String,
    ideLifeCyclePhase: Int,
    openWindows: Seq[String],
    openDocuments: Seq[String],
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class VersionControlClassDB(
    typeid: String,
    solution: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  case class VC_ActionClassDB (
    idesessionuuid: String,
    triggeredat: String,
    vctype : String,
    executedat: String,
    actiontype: Int
  )

  case class VC_ActionClass (
    vctype : String,
    executedat: String,
    actiontype: Int
  )

  case class VersionControlClass(
    typeid: String,
    actions: Seq[VC_ActionClass],
    solution: String,
    idesessionuuid: String,
    kaveversion: String,
    triggeredat: String,
    triggeredby: Int,
    duration: Option[String],
    activewindow: Option[String],
    activedocument: Option[String]
  )

  def toVersionControlClassDB(b: VersionControlClass) =
    VersionControlClassDB(b.typeid, b.solution, b.idesessionuuid,
      b.kaveversion, b.triggeredat, b.triggeredby, b.duration, b.activewindow, b.activedocument)

  def toVC_ActionsDB(b: VersionControlClass) =
    b.actions.map{ a =>
      VC_ActionClassDB(b.idesessionuuid, b.triggeredat, a.vctype, a.executedat, a.actiontype)
    }

  def toBuildClassDB(b: BuildClass) =
    BuildClassDB(b.typeid, b.scope, b.action, b.idesessionuuid,
      b.kaveversion, b.triggeredat, b.triggeredby, b.duration, b.activewindow, b.activedocument)
  def toTargetClassDB(b: BuildClass) =
    b.targets.map{ t =>
      TargetClassDB(b.idesessionuuid, b.triggeredat,
        t.targettype, t.project, t.projectConfiguration, t.platform, t.solutionConfiguration,
        t.startedAt, t.duration, t.successful)
    }

  def toTestClassDB(b: TestClass) =
    TestClassDB(b.typeid, b.wasaborted, b.idesessionuuid,
      b.kaveversion, b.triggeredat, b.triggeredby, b.duration, b.activewindow, b.activedocument)
  def toTestCaseClassDB(b: TestClass) =
    b.testcases.map{ t =>
      TestCaseClassDB(b.idesessionuuid, b.triggeredat,
        t.testcasetype, t.testmethod, t.parameters, t.duration, t.result)
    }

  def toIDEStateClassDB(b: IDEStateClass) =
    IDEStateClassDB(b.typeid, b.ideLifeCyclePhase, b.idesessionuuid,
      b.kaveversion, b.triggeredat, b.triggeredby, b.duration, b.activewindow, b.activedocument)

  def toIDEStateWinsDB(i: IDEStateClass) =
    i.openWindows.map{ w =>
      IDEStateAttDB(i.idesessionuuid, i.triggeredat, w)
    }

  def toIDEStateDocsDB(i: IDEStateClass) =
    i.openDocuments.map{ w =>
      IDEStateAttDB(i.idesessionuuid, i.triggeredat, w)
    }

  /*
   object BuildClassDB {
   def apply(bar: BuildClass) = new BuildClassDB(bar)
   }
   */
  class Commands(tag:Tag) extends Table[CommandsClass](tag, "commands") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def commandid = column[String]("commandid", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, commandid, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (CommandsClass.tupled, CommandsClass.unapply)
  }

  class Edits(tag:Tag) extends Table[EditClass](tag, "edits") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    //    def context2 = column[String]("context2", O.SqlType("TEXT"))
    def numberchanges = column[Int]("numberchanges", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def * = (typeid, numberchanges, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow) <> (EditClass.tupled, EditClass.unapply)
  }

  class Systems(tag:Tag) extends Table[SystemClass](tag, "systems") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def typeval = column[Int]("type", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, typeval, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (SystemClass.tupled, SystemClass.unapply)
  }

  class Finds(tag:Tag) extends Table[FindClass](tag, "finds") {
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def cancelled = column[Boolean]("type", O.SqlType("Boolean"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, cancelled, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (FindClass.tupled, FindClass.unapply)
  }



  class Activities(tag:Tag) extends Table[ActivityClass](tag, "activities") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[String]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (ActivityClass.tupled, ActivityClass.unapply)
  }

  class Tests(tag:Tag) extends Table[TestClassDB](tag, "tests") {

    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def wasaborted = column[Boolean]("wasaborted", O.SqlType("Boolean"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))

    def * = (typeid, wasaborted, idesessionuuid,
      kaveversion, triggeredat, triggeredby,
      duration, activewindow, activedocument) <> (TestClassDB.tupled, TestClassDB.unapply)
  }

  class TestCases(tag:Tag) extends Table[TestCaseClassDB](tag, "testcases") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def testcasetype = column[String]("testcasetype", O.SqlType("TEXT"))
    def testmethod = column[String]("testmethod", O.SqlType("TEXT"))
    def parameters = column[String]("parameters", O.SqlType("TEXT"))
    def duration = column[String]("duration", O.SqlType("TEXT"))
    def result = column[Int]("result", O.SqlType("Int"))
    def * = (idesessionuuid, triggeredat, testcasetype, testmethod,
      parameters, duration, result) <> (TestCaseClassDB.tupled, TestCaseClassDB.unapply)
  }

  class Builds(tag:Tag) extends Table[BuildClassDB](tag, "builds") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def scope = column[String]("scope", O.SqlType("TEXT"))
    def action = column[String]("action", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, scope, action, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (BuildClassDB.tupled, BuildClassDB.unapply)
  }



  class Targets(tag:Tag) extends Table[TargetClassDB](tag, "targets") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def targettype = column[String]("targettype", O.SqlType("TEXT"))
    def project = column[String]("project", O.SqlType("TEXT"))
    def projectConfiguration = column[String]("projectconf", O.SqlType("TEXT"))
    def platform = column[String]("platform", O.SqlType("TEXT"))
    def solutionConfiguration = column[String]("solutionconf", O.SqlType("TEXT"))
    def startedAt = column[String]("startedAt", O.SqlType("TEXT"))
    def duration = column[String]("duration", O.SqlType("TEXT"))
    def successful = column[Boolean]("successful", O.SqlType("Boolean"))
    def * = (idesessionuuid, triggeredat, targettype, project, projectConfiguration,
      platform, solutionConfiguration, startedAt, duration, successful) <> (TargetClassDB.tupled, TargetClassDB.unapply)
  }


  class Windows(tag:Tag) extends Table[WindowClass](tag, "windows") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def window = column[String]("window", O.SqlType("TEXT"))
    def action = column[Int]("action", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[String]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, window, action, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (WindowClass.tupled, WindowClass.unapply)
  }

  class Navs(tag:Tag) extends Table[NavClass](tag, "navigations") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def target = column[String]("target", O.SqlType("TEXT"))
    def location = column[String]("location", O.SqlType("TEXT"))
    def navType = column[Int]("navtype", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[String]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, target, location, navType, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (NavClass.tupled, NavClass.unapply)
  }


  class Debuggers(tag:Tag) extends Table[DebuggerClass](tag, "debuggers") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def mode = column[Int]("mode", O.SqlType("Int"))
    def reason = column[String]("reason", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, mode, reason, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (DebuggerClass.tupled, DebuggerClass.unapply)
  }

  class Solutions(tag:Tag) extends Table[SolutionClass](tag, "solutions") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def action = column[Int]("action", O.SqlType("Int"))
    def target = column[String]("target", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[String]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, action, target, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (SolutionClass.tupled, SolutionClass.unapply)
  }

  class Documents(tag:Tag) extends Table[DocumentClass](tag, "documents") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def document = column[Option[String]]("document", O.SqlType("TEXT"))
    def action = column[Int]("action", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[String]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, document, action, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (DocumentClass.tupled, DocumentClass.unapply)
  }

  class Users(tag:Tag) extends Table[UserClass](tag, "users") {
    
    //    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def profileid = column[String]("profileid", O.SqlType("TEXT"))
    def education = column[Int]("education", O.SqlType("Int"))
    def position = column[Int]("position", O.SqlType("Int"))
    def projectsCourses = column[Boolean]("projectsCourses", O.SqlType("Boolean"))
    def projectsPersonal  = column[Boolean]("projectsPersonal", O.SqlType("Boolean"))
    def projectsSharedSmall  = column[Boolean]("projectsSharedSmall", O.SqlType("Boolean"))
    def projectsSharedMedium  = column[Boolean]("projectsSharedMedium", O.SqlType("Boolean"))
    def projectsSharedLarge  = column[Boolean]("projectsSharedLarge", O.SqlType("Boolean"))
    def teamsSolo  = column[Boolean]("teamsSolo", O.SqlType("Boolean"))
    def teamsSmall  = column[Boolean]("teamsSmall", O.SqlType("Boolean"))
    def teamsMedium  = column[Boolean]("teamsMedium", O.SqlType("Boolean"))
    def teamsLarge  = column[Boolean]("teamsLarge", O.SqlType("Boolean"))
    def codeReviews  = column[Int]("codeReviews", O.SqlType("Int"))
    def programmingGeneral  = column[Int]("programmingGeneral", O.SqlType("Int"))
    def programmingCSharp  = column[Int]("programmingCSharp", O.SqlType("Int"))
    def comment  = column[String]("comment", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (profileid, education, position, projectsCourses,
      projectsPersonal, projectsSharedSmall, projectsSharedMedium, projectsSharedLarge, teamsSolo,
      teamsSmall, teamsMedium, teamsLarge, codeReviews, programmingGeneral,
      programmingCSharp, comment, idesessionuuid, kaveversion, triggeredat,
      triggeredby, activewindow, activedocument) <> (UserClass.tupled, UserClass.unapply)
  }

  class VersionControls(tag:Tag) extends Table[VersionControlClassDB](tag, "versioncontrols") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def solution = column[String]("solution", O.SqlType("TEXT"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, solution, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (VersionControlClassDB.tupled, VersionControlClassDB.unapply)
  }

  class VC_Actions(tag:Tag) extends Table[VC_ActionClassDB](tag, "vc_action") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def vctype = column[String]("vctype", O.SqlType("TEXT"))
    def executedat = column[String]("executedat", O.SqlType("TEXT"))
    def actiontype = column[Int]("actiontype", O.SqlType("TEXT"))
    def * = (idesessionuuid, triggeredat, vctype, executedat, actiontype) <>
    (VC_ActionClassDB.tupled, VC_ActionClassDB.unapply)
  }

  class IDEStates(tag:Tag) extends Table[IDEStateClassDB](tag, "idestates") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def typeid = column[String]("typeid", O.SqlType("TEXT"))
    def ideLifeCyclePhase = column[Int]("idelifecycle", O.SqlType("Int"))
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def kaveversion = column[String]("kaveversion", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def triggeredby = column[Int]("triggeredby", O.SqlType("Int"))
    def duration = column[Option[String]]("duration", O.SqlType("TEXT"))
    def activewindow = column[Option[String]]("activewindow", O.SqlType("TEXT"))
    def activedocument = column[Option[String]]("activedocument", O.SqlType("TEXT"))
    def * = (typeid, ideLifeCyclePhase, idesessionuuid, kaveversion, triggeredat, triggeredby, duration,
      activewindow, activedocument) <> (IDEStateClassDB.tupled, IDEStateClassDB.unapply)
  }

  class OpenWindows(tag:Tag) extends Table[IDEStateAttDB](tag, "openwindows") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def att = column[String]("openwindow", O.SqlType("TEXT"))
    def * = (idesessionuuid, triggeredat, att) <> (IDEStateAttDB.tupled, IDEStateAttDB.unapply)
  }

  class OpenDocuments(tag:Tag) extends Table[IDEStateAttDB](tag, "opendocuments") {
    //(String, String, String, String, String, String, String, String, String)]
    
    def idesessionuuid = column[String]("idesessionuuid", O.SqlType("TEXT"))
    def triggeredat = column[String]("triggeredat", O.SqlType("TEXT"))
    def att = column[String]("openwindow", O.SqlType("TEXT"))
    def * = (idesessionuuid, triggeredat, att) <> (IDEStateAttDB.tupled, IDEStateAttDB.unapply)
  }
  
  implicit val commandReads : Reads[CommandsClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "CommandId").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(CommandsClass.apply _)

  implicit val systemReads : Reads[SystemClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Type").read[Int] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(SystemClass.apply _)

  implicit val findReads : Reads[FindClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Cancelled").read[Boolean] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(FindClass.apply _)


  implicit val testCaseReads : Reads[TestCaseClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "TestMethod").read[String] and
      (JsPath \ "Parameters").read[String] and
      (JsPath \ "Duration").read[String] and
      (JsPath \ "Result").read[Int]
  )(TestCaseClass.apply _)

  implicit val testReads : Reads[TestClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "WasAborted").read[Boolean] and
      (JsPath \ "Tests").read[Seq[TestCaseClass]] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(TestClass.apply _)


  implicit val targetReads : Reads[TargetClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Project").read[String] and
      (JsPath \ "ProjectConfiguration").read[String] and
      (JsPath \ "Platform").read[String] and
      (JsPath \ "SolutionConfiguration").read[String] and
      (JsPath \ "StartedAt").read[String] and
      (JsPath \ "Duration").read[String] and
      (JsPath \ "Successful").read[Boolean]
  )(TargetClass.apply _)

  implicit val buildReads : Reads[BuildClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Scope").read[String] and
      (JsPath \ "Action").read[String] and
      (JsPath \ "Targets").read[Seq[TargetClass]] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(BuildClass.apply _)

  implicit val vc_ActionsReads : Reads[VC_ActionClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "ExecutedAt").read[String] and
      (JsPath \ "ActionType").read[Int]
  )(VC_ActionClass.apply _)

  implicit val versionControlReads : Reads[VersionControlClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Actions").read[Seq[VC_ActionClass]] and
      (JsPath \ "Solution").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String] and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(VersionControlClass.apply _)

  implicit val editReads : Reads[EditClass] = (
    (JsPath \ "$type").read[String] and
      //      (JsPath \ "Context2").read[String] and
      (JsPath \ "NumberOfChanges").read[Int] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String]
  )(EditClass.apply _)

  implicit val windowReads : Reads[WindowClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Window").read[String] and
      (JsPath \ "Action").read[Int] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").read[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(WindowClass.apply _)

  implicit val activityReads : Reads[ActivityClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").read[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(ActivityClass.apply _)

  implicit val navReads : Reads[NavClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Target").read[String] and
      (JsPath \ "Location").read[String] and
      (JsPath \ "TypeOfNavigation").read[Int] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").read[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(NavClass.apply _)

  implicit val debuggerReads : Reads[DebuggerClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Mode").read[Int] and
      (JsPath \ "Reason").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(DebuggerClass.apply _)

  implicit val solutionReads : Reads[SolutionClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Action").read[Int] and
      (JsPath \ "Target").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").read[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(SolutionClass.apply _)

  implicit val userReads : Reads[UserClass] = (
    (JsPath \ "ProfileId").read[String] and
      (JsPath \ "Education").read[Int] and
      (JsPath \ "Position").read[Int] and
      (JsPath \ "ProjectsCourses").read[Boolean] and
      (JsPath \ "ProjectsPersonal").read[Boolean] and
      (JsPath \ "ProjectsSharedSmall").read[Boolean] and
      (JsPath \ "ProjectsSharedMedium").read[Boolean] and
      (JsPath \ "ProjectsSharedLarge").read[Boolean] and
      (JsPath \ "TeamsSolo").read[Boolean] and
      (JsPath \ "TeamsSmall").read[Boolean] and
      (JsPath \ "TeamsMedium").read[Boolean] and
      (JsPath \ "TeamsLarge").read[Boolean] and
      (JsPath \ "CodeReviews").read[Int] and
      (JsPath \ "ProgrammingGeneral").read[Int] and
      (JsPath \ "ProgrammingCSharp").read[Int] and
      (JsPath \ "Comment").read[String] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "ActiveWindow").readNullable[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(UserClass.apply _)

  implicit val documentReads : Reads[DocumentClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "Document").readNullable[String] and
      (JsPath \ "Action").read[Int] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").read[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(DocumentClass.apply _)

  implicit val IDEStateReads : Reads[IDEStateClass] = (
    (JsPath \ "$type").read[String] and
      (JsPath \ "IDELifecyclePhase").read[Int] and
      (JsPath \ "OpenWindows").read[Seq[String]] and
      (JsPath \ "OpenDocuments").read[Seq[String]] and
      (JsPath \ "IDESessionUUID").read[String] and
      (JsPath \ "KaVEVersion").read[String] and
      (JsPath \ "TriggeredAt").read[String] and
      (JsPath \ "TriggeredBy").read[Int] and
      (JsPath \ "Duration").readNullable[String] and
      (JsPath \ "ActiveWindow").readNullable[String]  and
      (JsPath \ "ActiveDocument").readNullable[String]
  )(IDEStateClass.apply _)



  /*
   implicit object CommandsClasspReads extends Format[CommandsClass] {
   
   def reads(json: JsValue) = CommandsClass(
   (json \ "cid").as[String],
   (json \ "commandid").as[String],
   (json \ "idesessionuuid").as[String],
   (json \ "kaveversion").as[String],
   (json \ "triggeredat").as[String],
   (json \ "triggeredby").as[String],
   (json \ "duraction").as[String],
   (json \ "activewindow").as[String],
   (json \ "activedocument").as[String]
   )
   def writes(ts: CommandsClass) = JsObject(Seq())
   }

   */

  def open_DB(dbPath: String): Database = {
    val sqliConfig = new SQLiteConfig();
    sqliConfig.setJournalMode(SQLiteConfig.JournalMode.MEMORY)
    sqliConfig.setSynchronous(SQLiteConfig.SynchronousMode.OFF)

    val dbURL = "jdbc:sqlite:" + dbPath
    Database.forURL(dbURL, driver = "org.sqlite.JDBC", prop = sqliConfig.toProperties) //forConfig("sqlite")
  }


  val commands = TableQuery[Commands]
  val edits = TableQuery[Edits]
  val systems = TableQuery[Systems]
  val windows = TableQuery[Windows]
  val activities = TableQuery[Activities]
  val navs = TableQuery[Navs]
  val debuggers = TableQuery[Debuggers]
  val solutions = TableQuery[Solutions]
  val users = TableQuery[Users]
  val documents = TableQuery[Documents]
  val builds = TableQuery[Builds]
  val targets = TableQuery[Targets]
  val ideStates = TableQuery[IDEStates]
  val openWindows = TableQuery[OpenWindows]
  val openDocs = TableQuery[OpenDocuments]
  val versionControls = TableQuery[VersionControls]
  val vc_actions = TableQuery[VC_Actions]
  val tests = TableQuery[Tests]
  val testCases = TableQuery[TestCases]
  val finds = TableQuery[Finds]

  def create_schema(db:Database) = {
    println("Creating schema...")

    val schema = commands.schema ++ edits.schema ++ systems.schema ++
    windows.schema ++ activities.schema ++ navs.schema ++ debuggers.schema ++ solutions.schema ++ users.schema ++
    documents.schema ++ builds.schema ++ targets.schema ++
    ideStates.schema ++ openDocs.schema ++ openWindows.schema ++
    versionControls.schema ++ vc_actions.schema ++ tests.schema ++ testCases.schema ++ finds.schema

    try {
      Await.result(db.run(DBIO.seq(
        schema.drop
      )), Duration.Inf)
    }
    catch {
      case _: Throwable => println(Console.RED +  "Unable to drop tables " + Console.RESET )
    }

    try {
      Await.result(db.run(DBIO.seq(
        schema.create
      )), Duration.Inf)
    }
    catch {
      case _: Throwable => println(Console.RED +  "Unable to create tables " + Console.RESET )
    }
  }

  def Insert_Commands(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting commands")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.CommandEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      json.as[CommandsClass]
    }.toList


    val insert = DBIO.seq(
      commands ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Windows(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting windows")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.WindowEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[WindowClass]
    }.toList
    val insert = DBIO.seq(
      windows ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Activities(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting activities")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.ActivityEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[ActivityClass]
    }.toList
    println("To insert activites" , toInsert.size)
    val insert = DBIO.seq(
      activities ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }


  def Insert_Edits(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting edits")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.EditEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[EditClass]
    }.toList


    val insert = DBIO.seq(
      edits ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Systems(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting systems")
    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.SystemEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[SystemClass]
    }.toList


    val insert = DBIO.seq(
      systems ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Navs(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting navs")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.NavigationEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[NavClass]
    }.toList


    val insert = DBIO.seq(
      navs ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Debuggers(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting debugger")

    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.DebuggerEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[DebuggerClass]
    }.toList


    val insert = DBIO.seq(
      debuggers ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Solutions(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting solutions")
    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.SolutionEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[SolutionClass]
    }.toList


    val insert = DBIO.seq(
      solutions ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Users(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting users")
    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.UserProfiles.UserProfileEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[UserClass]
    }.toList


    val insert = DBIO.seq(
      users ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Documents(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting documents")
    val toInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.DocumentEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[DocumentClass]
    }.toList


    val insert = DBIO.seq(
      documents ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_VersionControls(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting version controls")

    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VersionControlEvents.VersionControlEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[VersionControlClass]
    }.toList

    val toInsert = tuplesToInsert.map(toVersionControlClassDB(_))
    val actionsToInsert = tuplesToInsert.map(toVC_ActionsDB(_)).flatten

    val insert = DBIO.seq(
      versionControls ++= toInsert,
      vc_actions ++= actionsToInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }


  def Insert_Builds(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting builds")

    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.BuildEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[BuildClass]
    }.toList

    val toInsert = tuplesToInsert.map(toBuildClassDB(_))
    val targetsToInsert = tuplesToInsert.map(toTargetClassDB(_)).flatten

    val insert = DBIO.seq(
      builds ++= toInsert,
      targets ++= targetsToInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_IDEStates(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting ide states")


    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.IDEStateEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //        println(Json.prettyPrint(t._2))
      json.as[IDEStateClass]
    }.toList

    val toInsert = tuplesToInsert.map(toIDEStateClassDB(_))
    val winsToInsert = tuplesToInsert.map(toIDEStateWinsDB(_)).flatten
    val docsToInsert = tuplesToInsert.map(toIDEStateDocsDB(_)).flatten

    val insert = DBIO.seq(
      openWindows ++= winsToInsert,
      openDocs ++= docsToInsert,
      ideStates ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Finds(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println("Inserting finds")

    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.VisualStudio.FindEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //println(Json.prettyPrint(t._2))
      json.as[FindClass]
    }.toList
    val insert = DBIO.seq(
      finds ++= tuplesToInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }

  def Insert_Completions(db:Database, tuples: Seq[(JsValue, JsValue)]) = {
    println(("Completions. To be implemented..................................................................", tuples.size ))

    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.CompletionEvents.CompletionEvent, KaVE.Commons\"").map { t=>
      val json = t._2
      //println(Json.prettyPrint(t._2))
      //json.as[IDEStateClass]
    }.toList
    
/*
    val toInsert = tuplesToInsert.map(toIDEStateClassDB(_))
    val winsToInsert = tuplesToInsert.map(toIDEStateWinsDB(_)).flatten
    val docsToInsert = tuplesToInsert.map(toIDEStateDocsDB(_)).flatten

    val insert = DBIO.seq(
      openWindows ++= winsToInsert,
      openDocs ++= docsToInsert,
      ideStates ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
 */
  }

  def Insert_Tests(db:Database, tuples: Seq[(JsValue, JsValue)]) = {

    val tuplesToInsert = tuples.filter(_._1.toString == "\"KaVE.Commons.Model.Events.TestRunEvents.TestRunEvent, KaVE.Commons\"").map { t=>
      val json = t._2
//      println(Json.prettyPrint(t._2))
      json.as[TestClass]
    }.toList
    
    val toInsert = tuplesToInsert.map(toTestClassDB(_))
    val casesToInsert = tuplesToInsert.map(toTestCaseClassDB(_)).flatten

    val insert = DBIO.seq(
      testCases ++= casesToInsert,
      tests ++= toInsert
    )
    Await.result(db.run(insert), Duration.Inf)
  }



  def Insert(db:Database, tuples: Seq[(JsValue, JsValue)]) = {

    Insert_Completions(db, tuples)

    Insert_Activities(db, tuples)
    Insert_Commands(db, tuples)
// completion
    Insert_Navs(db, tuples)
    Insert_Systems(db, tuples)

    Insert_Tests(db, tuples)
    Insert_Users(db, tuples)
    Insert_VersionControls(db, tuples)

    Insert_Builds(db, tuples)
    Insert_Debuggers(db, tuples)
    Insert_Documents(db, tuples)
    Insert_Edits(db, tuples)

    Insert_Finds(db, tuples)

    Insert_IDEStates(db, tuples)
    Insert_Solutions(db, tuples)
    Insert_Windows(db, tuples)
  }

  def getRecursiveListOfFiles(dir: File): Seq[File] = {
    val these = dir.listFiles
    these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
  }
  //////////////////////////////////////////////

  def Process_Zip_File(db:Database, zipFile: File) {

    println(("To process zip file: ", zipFile.getPath(), " size ", zipFile.length))
    val rootzip = new java.util.zip.ZipFile(zipFile.getPath())

    val entries = rootzip.entries.asScala

    val tuples = entries.map{ e =>
      //      println(e)
      val is = rootzip.getInputStream(e)
      val source = scala.io.Source.fromInputStream(is).mkString
      val json: JsValue = Json.parse(source)
      val jtype = (json \ "$type" ).get
      (jtype, json)
    }.toSeq

    tuples.toList.map(_._1).groupBy(identity).mapValues(_.size).toSeq.sortWith(_._1.toString < _._1.toString).foreach{ t =>
      println(t)
    }


    Insert(db, tuples)
  }

  def Process_File(db:Database, path: String) {

      val file = new File(path)
      val files:Seq[File] =
        if (file.isDirectory) {
          getRecursiveListOfFiles(new File(path))
            .filter{s => s.getName().matches(".+zip$")}.sorted
        } else {
          if (!file.getName.matches(".+zip$"))
            throw new IllegalStateException("Filename is not a zipfile: "+ path);
          Seq(file)
        }

    files.foreach{ f =>
      println("Processing file ", f.getName)
      Process_Zip_File(db, f)
    }
  }

  def main(args: Array[String]) {

    if (args.size == 0) {
      throw new IllegalStateException("No options passed. Usage <command> dbPath [filesToProces]");
    }
    val dbPath = args(0)
    val fileDB = new File(dbPath)
    val createSchema = ! (fileDB.exists)

    val db = open_DB(dbPath)

    if (createSchema)
      create_schema(db)
    
    val paths = args.drop(1)
    paths.foreach{ p =>
      Process_File(db, p)
    }

    db.close

  }
}


