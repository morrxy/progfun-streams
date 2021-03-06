package streams

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import Bloxorz._

@RunWith(classOf[JUnitRunner])
class BloxorzSuite extends FunSuite {

  trait SolutionChecker extends GameDef with Solver with StringParserTerrain {
    /**
     * This method applies a list of moves `ls` to the block at position
     * `startPos`. This can be used to verify if a certain list of moves
     * is a valid solution, i.e. leads to the goal.
     */
    def solve(ls: List[Move]): Block =
      ls.foldLeft(startBlock) { case (block, move) => move match {
        case Left => block.left
        case Right => block.right
        case Up => block.up
        case Down => block.down
      }
    }
  }

  trait Level1 extends SolutionChecker {
      /* terrain for level 1*/

    val level =
    """ooo-------
      |oSoooo----
      |ooooooooo-
      |-ooooooooo
      |-----ooToo
      |------ooo-""".stripMargin

    val optsolution = List(Right, Right, Down, Right, Right, Right, Down)
  }

  trait Level2 extends SolutionChecker {
    /* terrain for level 2*/

    val level =
      """ooo-------
        |oSooTo----""".stripMargin

    val optsolution = List(Right, Right)
  }

  test("terrain function level 1") {
    new Level1 {
      assert(terrain(Pos(0,0)), "0,0")
      assert(!terrain(Pos(4,11)), "4,11")
    }
  }

  test("findChar level 1") {
    new Level1 {
      assert(startPos == Pos(1,1))
      assert(goal == Pos(4, 7))
    }
  }

  test("isStanding") {
    new Level2 {
      assert(Block(Pos(1,1),Pos(1,1)).isStanding)
      assert(Block(Pos(1,4),Pos(1,4)).isStanding)
      assert(!Block(Pos(1,2),Pos(1,3)).isStanding)
    }
  }

  test("isLegal") {
    new Level2 {
      assert(Block(Pos(1,1),Pos(1,1)).isLegal)
      assert(Block(Pos(1,4),Pos(1,4)).isLegal)
      assert(!Block(Pos(0,2),Pos(0,3)).isLegal)
      assert(!Block(Pos(2,2),Pos(2,3)).isLegal)
    }
  }

  test("neighborsWithHistory") {
    new Level1 {
      val s = neighborsWithHistory(Block(Pos(1,1),Pos(1,1)), List(Left,Up))
      assert(s.toSet === Set(
        (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
        (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
      ))
    }

    new Level2 {
      val s = neighborsWithHistory(Block(Pos(1,2),Pos(1,3)), List(Right))
      assert(s.toSet === Set(
        (Block(Pos(1,1),Pos(1,1)), List(Left, Right)),
        (Block(Pos(1,4), Pos(1,4)), List(Right, Right))
      ))
    }
  }

  test("newNeighborsOnly") {
    new Level1 {
      val s = newNeighborsOnly(
        Set(
          (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
          (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
        ).toStream,

        Set(Block(Pos(1,2),Pos(1,3)), Block(Pos(1,1),Pos(1,1)))
      )

      assert(s.toSet === Set(
        (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
      ))
      assert(s === Set(
        (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
      ).toStream)
    }
  }

  test("from") {
    new Level1 {
      val initial = Stream((startBlock, Nil))
      val explored = Set(startBlock)
      val s = from(initial, explored)
      println("level1 all path: " + s.toList)
      println("level1 all path length: " + s.toList.length)

      println("level1 pathsFromStart length: " + pathsFromStart.toList.length)
    }

    new Level2 {
      val initial = Stream((startBlock, Nil))
      val explored = Set(startBlock)
      val s = from(initial, explored)
      println("level2 all path: " + s.toList)
      println("level2 all path length: " + s.toList.length)

      println("level2 pathsFromStart length: " + pathsFromStart.toList.length)
    }
  }

  test("solution") {
    new Level1 {
      assert(solution == optsolution)
    }
  }

  test("optimal solution for level 1") {
    new Level1 {
      assert(solve(solution) == Block(goal, goal))
    }
  }

  test("optimal solution length for level 1") {
    new Level1 {
      assert(solution.length == optsolution.length)
    }
  }
}
