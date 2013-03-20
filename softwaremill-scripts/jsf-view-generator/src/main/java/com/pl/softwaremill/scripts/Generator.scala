package eu.vegasoft

import reflect.Field
import java.io.{OutputStreamWriter, Writer}
import xml._
import za.co.fnb.commercial.dms.entity._

/**
 *XML Created by Pawel Stawicki on May 17, 2011 10:47:06 PM
 */

object Generator extends Application {

  //Set class you want to create view fow
  val entityClass = classOf[FinancialInfo]

  //Set bean EL name to prefix fields in view
  val beanName = "pcDetails.lastFinancialInfo"

  val xml = {
    for(field <- entityClass.getDeclaredFields)
    yield

      <td>
          <h:outputText value={field.getName} />
      </td>
      <td>
          <h:inputText id={field.getName}
                       disabled="true"
                       value={"#{" + beanName + "." + field.getName + "}"} />

      </td>
  }

//            <h:message id={field.getName + "Msg"}
//                     for={field.getName}
//                     errorClass="invalid" />

  val folded = xml.foldLeft(<root/>)((b:Node, nodeb:NodeBuffer) =>
    addChild(b, (nodeb.foldLeft(<tr />)((b:Node, node:Node) => addChild(b, node))))
  )

  val printer = new PrettyPrinter(200, 2) {
    override protected def traverse(node: Node, pscope: NamespaceBinding, ind: Int) =
      node match {
        case n:Elem if n.child.size == 0 => makeBox(ind, leafTag(n))
        case _ => super.traverse(node, pscope, ind)
      }
  }


  println(printer.format(folded))

  def addChild(n: Node, newChild: Node) = n match {
    case Elem(prefix, label, attribs, scope, child @ _*) => Elem(prefix, label, attribs, scope, child ++ newChild : _*)
    case _ => error("Can only add children to elements!")
  }
}