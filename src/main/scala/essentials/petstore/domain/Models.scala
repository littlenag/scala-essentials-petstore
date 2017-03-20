package essentials.petstore.domain


/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object Models {

  import java.util.UUID

  // TODO make age more type safe
  // TODO make sex more type safe

  case class Pet(id:UUID, name:String, species:String, age:Int, sex:String, fixed:Boolean)

  case class Owner(name:String, address:String)

}
