package essentials.petstore.domain

import org.joda.time.DateTime

// TODO make age more type safe
// TODO make species more type safe

case class PetRecord(adoptionPapers: Option[String], lastVetVisit: Option[DateTime], petId: Long, id: Option[Long] = None)

case class Pet(name: String, species: String, age: Int, fixed: Boolean, id: Option[Long] = None)
