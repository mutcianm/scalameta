// NOTE: unlike other tests, this one is not in `package scala.meta`
// because we don't want to pollute the scope of code snippets inside `typecheckError`

import org.scalatest._
import org.scalameta.tests._

class PublicSuite extends FunSuite {
  test("macro APIs without import") {
    assert(typecheckError("""
      warning("hello world!")
    """) === "not found: value warning")
  }

  test("macro APIs without context") {
    assert(typecheckError("""
      import scala.meta._
      warning("hello world!")
    """) === "this method requires an implicit scala.meta.macros.Context")
  }

  test("macro APIs when everything's correct") {
    assert(typecheckError("""
      import scala.meta._
      implicit val c: scala.meta.macros.Context = ???
      warning("hello world!")
    """) === "")
  }

  // TODO: this error is somewhat confusing
  test("macro context APIs") {
    assert(typecheckError("""
      (??? : scala.meta.macros.Context).warning("hello world!")
    """) === "method warning in trait Context cannot be accessed in scala.meta.macros.Context")
  }

  test("quasiquotes without import") {
    assert(typecheckError("""
      q"hello"
    """) === "value q is not a member of StringContext")
  }

  test("quasiquotes without any dialect") {
    assert(typecheckError("""
      import scala.meta._
      q"hello"
    """) === "don't know what dialect to use here (to fix this, import something from scala.dialects, e.g. scala.meta.dialects.Scala211)")
  }

  test("quasiquotes without static dialect") {
    assert(typecheckError("""
      import scala.meta._
      implicit val dialect: scala.meta.Dialect = ???
      q"hello"
    """) === "can't use the dialect dialect in quasiquotes")
  }

  test("quasiquotes when everything's correct (static dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      q"hello"
    """) === "")
  }

  test("quasiquotes when everything's correct (static context)") {
    assert(typecheckError("""
      import scala.meta._
      trait MyContext extends scala.meta.semantic.Context {
        def dialect: scala.meta.dialects.Scala211.type = scala.meta.dialects.Scala211
      }
      implicit val c: MyContext = ???
      q"hello"
    """) === "")
  }

  test("semantic APIs without import") {
    assert(typecheckError("""
      (??? : scala.meta.Ref).defn
    """) === "this method requires an implicit scala.meta.semantic.Context")
  }

  test("semantic APIs without context") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Ref).defn
    """) === "this method requires an implicit scala.meta.semantic.Context")
  }

  test("semantic APIs when everything's correct") {
    assert(typecheckError("""
      import scala.meta._
      implicit val c: scala.meta.semantic.Context = ???
      (??? : Ref).defn
    """) === "")
  }

  test("Tree.desugar") {
    assert(typecheckError("""
      import scala.meta._
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Tree).desugar
    """) === "value desugar is not a member of scala.meta.Tree")
  }

  test("Term.desugar") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      ((??? : api.Term).desugar): api.Term
      ((??? : impl.Term).desugar): api.Term
    """) === "")
  }

  test("Tree.tpe") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Tree).tpe
    """) === "value tpe is not a member of scala.meta.Tree")
  }

  test("Type.tpe") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      t"List".tpe
    """) === "value tpe is not a member of meta.internal.ast.Type.Name")
  }

  test("Term.tpe") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      ((??? : api.Term).tpe): api.Type
      ((??? : impl.Term).tpe): api.Type
      q"x".tpe: api.Type
    """) === "")
  }

  test("Member.tpe") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      ((??? : api.Member).tpe): api.Type
      ((??? : impl.Member).tpe): api.Type
      q"class C".tpe: api.Type
    """) === "")
  }

  test("Tree.defn") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Tree).defn
    """) === "value defn is not a member of scala.meta.Tree")
  }

  test("Type.Apply.defn") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      t"List[Int]".defn
    """) === "value defn is not a member of meta.internal.ast.Type.Apply")
  }

  test("Ref.defn") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Ref).defn: api.Member
      (??? : api.Term.Ref).defn: api.Member.Term
      (??? : api.Type.Ref).defn: api.Member
      (??? : impl.Ref).defn: api.Member
      (??? : impl.Term.Ref).defn: api.Member.Term
      (??? : impl.Type.Ref).defn: api.Member
      q"x".defns: scala.collection.immutable.Seq[api.Member.Term]
      q"x".defn: api.Member.Term
      t"x".defns: scala.collection.immutable.Seq[api.Member]
      t"x".defn: api.Member
      q"class C".name.defn: api.Member
      q"class C".source.name.defn: api.Member
      q"object M".name.defn: api.Member.Term
      q"object M".source.name.defn: api.Member.Term
    """) === "")
  }

  test("Tree.members") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Tree).members
    """) === "value members is not a member of scala.meta.Tree")
  }

  test("Term.members") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      q"scala".members
    """) === "value members is not a member of meta.internal.ast.Term.Name")
  }

  test("Type.members") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Type).members: scala.collection.immutable.Seq[api.Member]
      (??? : impl.Type).members: scala.collection.immutable.Seq[api.Member]
      t"List".members: scala.collection.immutable.Seq[api.Member]
      q"List".tpe.members: scala.collection.immutable.Seq[api.Member]
    """) === "")
  }

  test("Member.members") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      implicit val c: scala.meta.semantic.Context = ???
      import scala.{meta => api}
      import scala.meta.internal.{ast => impl}
      (??? : api.Member).members: scala.collection.immutable.Seq[api.Member]
      (??? : impl.Member).members: scala.collection.immutable.Seq[api.Member]
      t"List".defs("head"): api.Member.Term
      t"List".defs("head").paramss: scala.collection.immutable.Seq[scala.collection.immutable.Seq[api.Member.Term]]
      t"List".defn.tparams: scala.collection.immutable.Seq[api.Member.Type]
    """) === "")
  }

  // TODO: this error is somewhat confusing
  test("internal helpers of semantic APIs") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Member).internalAll(???)
    """) === "method internalAll in class SemanticScopeOps cannot be accessed in meta.SemanticScopeOps")
  }

  // TODO: this error is somewhat confusing
  test("semantic context APIs (opaque)") {
    assert(typecheckError("""
      (??? : scala.meta.semantic.Context).isSubType(???, ???)
    """) === "method isSubType in trait Context cannot be accessed in scala.meta.semantic.Context")
  }

  test("semantic context APIs (the only transparent one)") {
    assert(typecheckError("""
      (??? : scala.meta.semantic.Context).dialect
    """) === "")
  }

  test("parse without import") {
    assert(typecheckError("""
      "".parse[scala.meta.Term]
    """) === "value parse is not a member of String")
  }

  test("parse without origin-likeness") {
    assert(typecheckError("""
      import scala.meta._
      1.parse[Term]
    """) === "don't know how to convert Int to scala.meta.Origin")
  }

  test("parse without dialect") {
    assert(typecheckError("""
      import scala.meta._
      "".parse[Term]
    """) === "don't know what dialect to use here (to fix this, import something from scala.dialects, e.g. scala.meta.dialects.Scala211)")
  }

  test("parse without parseability") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      "".parse[Int]
    """) === "don't know how to parse Int (if you're sure that Int is parseable, double-check that you've imported a dialect, e.g. scala.meta.dialects.Scala211)")
  }

  test("parse when everything's correct (static dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      "".parse[Term]
    """) === "")
  }

  test("parse when everything's correct (dynamic dialect)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val dialect: scala.meta.Dialect = ???
      "".parse[Term]
    """) === "")
  }

  test("parse when everything's correct (static context)") {
    assert(typecheckError("""
      import scala.meta._
      trait MyContext extends scala.meta.semantic.Context {
        def dialect: scala.meta.dialects.Scala211.type = scala.meta.dialects.Scala211
      }
      implicit val c: MyContext = ???
      "".parse[Term]
    """) === "")
  }

  test("parse when everything's correct (dynamic context)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val c: scala.meta.semantic.Context = ???
      "".parse[Term]
    """) === "")
  }

  test("tokens without import") {
    assert(typecheckError("""
      "".tokens
    """) === "value tokens is not a member of String")
  }

  test("tokens without origin-likeness") {
    assert(typecheckError("""
      import scala.meta._
      1.tokens
    """) === "don't know how to convert Int to scala.meta.Origin")
  }

  test("tokens without dialect") {
    assert(typecheckError("""
      import scala.meta._
      "".tokens
    """) === "don't know what dialect to use here (to fix this, import something from scala.dialects, e.g. scala.meta.dialects.Scala211)")
  }

  test("tokens when everything's correct (static dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      "".tokens
    """) === "")
  }

  test("tokens when everything's correct (dynamic dialect)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val dialect: scala.meta.Dialect = ???
      "".tokens
    """) === "")
  }

  test("tokens when everything's correct (static context)") {
    assert(typecheckError("""
      import scala.meta._
      trait MyContext extends scala.meta.semantic.Context {
        def dialect: scala.meta.dialects.Scala211.type = scala.meta.dialects.Scala211
      }
      implicit val c: MyContext = ???
      "".tokens
    """) === "")
  }

  test("tokens when everything's correct (dynamic context)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val c: scala.meta.semantic.Context = ???
      "".tokens
    """) === "")
  }

  test("show[Code] without import") {
    assert(typecheckError("""
      (??? : scala.meta.Tree).show[Code]
    """) === "not found: type Code")
  }

  test("show[Code] without dialect") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Tree).show[Code]
    """) === "don't know how to show[Code] for scala.meta.Tree (if you're prettyprinting a tree, be sure to import a dialect, e.g. scala.meta.dialects.Scala211)")
  }

  test("show[Code] when everything's correct (static dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.dialects.Scala211
      (??? : Tree).show[Code]
    """) === "")
  }

  test("show[Code] when everything's correct (dynamic dialect)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val dialect: scala.meta.Dialect = ???
      (??? : Tree).show[Code]
    """) === "")
  }

  test("show[Code] when everything's correct (static context)") {
    assert(typecheckError("""
      import scala.meta._
      trait MyContext extends scala.meta.semantic.Context {
        def dialect: scala.meta.dialects.Scala211.type = scala.meta.dialects.Scala211
      }
      implicit val c: MyContext = ???
      (??? : Tree).show[Code]
    """) === "")
  }

  test("show[Code] when everything's correct (dynamic context)") {
    assert(typecheckError("""
      import scala.meta._
      implicit val c: scala.meta.semantic.Context = ???
      (??? : Tree).show[Code]
    """) === "")
  }

  test("show[Raw] without import") {
    assert(typecheckError("""
      (??? : scala.meta.Tree).show[Raw]
    """) === "not found: type Raw")
  }

  test("show[Raw] when everything's correct") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Tree).show[Raw]
    """) === "")
  }

  test("show[Summary] without import - 1") {
    assert(typecheckError("""
      (??? : scala.meta.Tree).show[Summary]
    """) === "not found: type Summary")
  }

  test("show[Summary] without import - 2") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Tree).show[Summary]
    """) === "not found: type Summary")
  }

  test("show[Summary] without dialect") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.ui.Summary
      (??? : Tree).show[Summary]
    """) === "don't know how to show[Summary] for scala.meta.Tree (if you're prettyprinting a tree, be sure to import a dialect, e.g. scala.meta.dialects.Scala211)")
  }

  test("show[Summary] when everything's correct (static dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.ui.Summary
      import scala.meta.dialects.Scala211
      (??? : Tree).show[Summary]
    """) === "")
  }

  test("show[Summary] when everything's correct (dynamic dialect)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.ui.Summary
      implicit val dialect: scala.meta.Dialect = ???
      (??? : Tree).show[Summary]
    """) === "")
  }

  test("show[Summary] when everything's correct (static context)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.ui.Summary
      trait MyContext extends scala.meta.semantic.Context {
        def dialect: scala.meta.dialects.Scala211.type = scala.meta.dialects.Scala211
      }
      implicit val c: MyContext = ???
      (??? : Tree).show[Summary]
    """) === "")
  }

  test("show[Summary] when everything's correct (dynamic context)") {
    assert(typecheckError("""
      import scala.meta._
      import scala.meta.ui.Summary
      implicit val c: scala.meta.semantic.Context = ???
      (??? : Tree).show[Summary]
    """) === "")
  }

  test("show[Semantics] without import") {
    assert(typecheckError("""
      (??? : scala.meta.Tree).show[Semantics]
    """) === "not found: type Semantics")
  }

  test("show[Semantics] when everything's correct") {
    assert(typecheckError("""
      import scala.meta._
      (??? : Tree).show[Semantics]
    """) === "")
  }
}
