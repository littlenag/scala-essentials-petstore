package essentials.petstore.domain

// TODO make age more type safe
// TODO make species more type safe

case class Pet(name:String, species:String, age:Int, fixed:Boolean, id:Long = 0L)
