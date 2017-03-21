package essentials.petstore.domain


/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object Models {

  // TODO make age more type safe
  // TODO make sex more type safe

  case class Pet(id:Long, name:String, species:String, age:Int, sex:String, fixed:Boolean)

  case class Owner(name:String, address:String)

}
