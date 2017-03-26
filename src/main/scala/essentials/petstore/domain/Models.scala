package essentials.petstore.domain


// TODO make age more type safe
// TODO make sex more type safe

case class Pet(name:String, species:String, age:Int, sex:String, fixed:Boolean, id:Long = 0L)

case class Owner(name:String, address:String)
