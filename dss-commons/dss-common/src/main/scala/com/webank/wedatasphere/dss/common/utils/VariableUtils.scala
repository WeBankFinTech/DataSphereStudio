/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.common.utils

import java.text.SimpleDateFormat
import java.util
import java.util.{Calendar, Date}

import org.apache.linkis.common.exception.LinkisCommonErrorException
import org.apache.linkis.common.utils.{Logging, Utils}
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.DateUtils

import scala.collection.mutable
import scala.collection.convert.WrapAsScala._
import scala.util.matching.Regex

object VariableUtils extends Logging {

  val RUN_DATE = "run_date"
  val RUN_TODAY = "run_today"
  val RUN_MON = "run_mon"

  var run_today: CustomDateType = null
  var run_mon: CustomMonType = null

  val dateFormatLocal = new ThreadLocal[SimpleDateFormat]() {
    override protected def initialValue = new SimpleDateFormat("yyyyMMdd")
  }

  val dateFormatStdLocal = new ThreadLocal[SimpleDateFormat]() {
    override protected def initialValue = new SimpleDateFormat("yyyy-MM-dd")
  }

  val dateFormatMonLocal = new ThreadLocal[SimpleDateFormat]() {
    override protected def initialValue = new SimpleDateFormat("yyyyMM")
  }

  val dateFormatMonStdLocal = new ThreadLocal[SimpleDateFormat]() {
    override protected def initialValue = new SimpleDateFormat("yyyy-MM")
  }

  val codeReg: Regex = "\\$\\{\\s*[A-Za-z][A-Za-z0-9_\\.]*\\s*[\\+\\-\\*/]?\\s*[A-Za-z0-9_\\.]*\\s*\\}".r
  val calReg: Regex = "(\\s*[A-Za-z][A-Za-z0-9_\\.]*\\s*)([\\+\\-\\*/]?)(\\s*[A-Za-z0-9_\\.]*\\s*)".r

  def replace(replaceStr: String): String = replace(replaceStr, new util.HashMap[String, Any](0))

  def replace(replaceStr: String, variables: util.Map[String, Any]): String = {
    val nameAndType = mutable.Map[String, VariableType]()
    var run_date: CustomDateType = null
    variables foreach {
      case (RUN_DATE, value) if nameAndType.get(RUN_DATE).isEmpty =>
        val run_date_str = value.asInstanceOf[String]
        if (StringUtils.isNotEmpty(run_date_str)) {
          run_date = new CustomDateType(run_date_str, false)
          nameAndType(RUN_DATE) = DateType(run_date)
        }
      case (key, value: String) if !nameAndType.contains(key) && StringUtils.isNotEmpty(value) =>
        nameAndType(key) = Utils.tryCatch[VariableType](DoubleValue(value.toDouble))(_ => StringType(value))
      case _ =>
    }
    if(!nameAndType.contains(RUN_DATE) || null == run_date){
      run_date = new CustomDateType(getYesterday(false), false)
      nameAndType(RUN_DATE) = DateType(new CustomDateType(run_date.toString, false))
    }
    initAllDateVars(run_date, nameAndType)
    parserVar(replaceStr, nameAndType)
  }

  private def initAllDateVars(run_date: CustomDateType, nameAndType: mutable.Map[String, VariableType]): Unit = {

    nameAndType("run_date_std") = DateType(new CustomDateType(run_date.getStdDate))
    nameAndType("run_month_begin") = MonthType(new CustomMonthType(run_date.toString, false))
    nameAndType("run_month_begin_std") = MonthType(new CustomMonthType(run_date.toString))
    nameAndType("run_month_end") = MonthType(new CustomMonthType(run_date.toString, false, true))
    nameAndType("run_month_end_std") = MonthType(new CustomMonthType(run_date.toString, true, true))

    /*
   Variables based on run_today
   */
//    if (nameAndType.get(RUN_TODAY).isEmpty || null == run_today) {
//      run_today = new CustomDateType(getToday(false), false)
//      nameAndType(RUN_TODAY) = DateType(new CustomDateType(run_today.toString, false))
//    }
    run_today = new CustomDateType(new CustomDateType(run_date.toString, false) + 1, false)
    nameAndType(RUN_TODAY)=DateType(new CustomDateType(run_today.toString, false))
    nameAndType("run_today_std") = DateType(new CustomDateType(run_today.getStdDate))
    nameAndType("run_month_now_begin") = MonthType(new CustomMonthType(new CustomMonthType(run_today.toString, false) - 1, false))
    nameAndType("run_month_now_begin_std") = MonthType(new CustomMonthType(new CustomMonthType(run_today.toString, false) - 1))
    nameAndType("run_month_now_end") = MonthType(new CustomMonthType(new CustomMonthType(run_today.toString, false) - 1, false, true))
    nameAndType("run_month_now_end_std") = MonthType(new CustomMonthType(new CustomMonthType(run_today.toString, false) - 1, true, true))

//    if (nameAndType.get(RUN_MON).isEmpty || null == run_mon) {
//      run_mon = new CustomMonType(getMonthDay(false), false)
//      nameAndType(RUN_MON) = MonType(new CustomMonType(run_mon.toString, false))
//    }

    run_mon = new CustomMonType(new CustomMonthType(run_today.toString, false) - 1, false)
    nameAndType(RUN_MON) = MonType(new CustomMonType(run_mon.toString, false))

    nameAndType("run_mon_std") = MonType(new CustomMonType(run_mon.toString, true, false))
    nameAndType("run_mon_start") = MonType(new CustomMonType(run_mon.toString, false, false))
    nameAndType("run_mon_start_std") = MonType(new CustomMonType(run_mon.toString, true, false))
    nameAndType("run_mon_end") = MonType(new CustomMonType(run_mon.toString, false, true))
    nameAndType("run_mon_end_std") = MonType(new CustomMonType(run_mon.toString, true, true))

    nameAndType("run_quarter_begin") = QuarterType(new CustomQuarterType(run_date.toString, false))
    nameAndType("run_quarter_begin_std") = QuarterType(new CustomQuarterType(run_date.toString))
    nameAndType("run_quarter_end") = QuarterType(new CustomQuarterType(run_date.toString, false, true))
    nameAndType("run_quarter_end_std") = QuarterType(new CustomQuarterType(run_date.toString, true, true))

    nameAndType("run_half_year_begin") = HalfYearType(new CustomHalfYearType(run_date.toString, false))
    nameAndType("run_half_year_begin_std") = HalfYearType(new CustomHalfYearType(run_date.toString))
    nameAndType("run_half_year_end") = HalfYearType(new CustomHalfYearType(run_date.toString, false, true))
    nameAndType("run_half_year_end_std") = HalfYearType(new CustomHalfYearType(run_date.toString, true, true))

    nameAndType("run_year_begin") = YearType(new CustomYearType(run_date.toString, false))
    nameAndType("run_year_begin_std") = YearType(new CustomYearType(run_date.toString))
    nameAndType("run_year_end") = YearType(new CustomYearType(run_date.toString, false, true))
    nameAndType("run_year_end_std") = YearType(new CustomYearType(run_date.toString, true, true))

  }

  /**
    * Parse and replace the value of the variable
    * 1.Get the expression and calculations
    * 2.Print user log
    * 3.Assemble code
    *
    * @param replaceStr        : replaceStr
    * @param nameAndType : variable name and Type
    * @return
    */
  private def parserVar(replaceStr: String, nameAndType: mutable.Map[String, VariableType]): String = {
    val parseCode = new StringBuilder
    val codes = codeReg.split(replaceStr)
    var i = 0
    codeReg.findAllIn(replaceStr).foreach{ str =>
      i = i + 1
      calReg.findFirstMatchIn(str).foreach{ ma =>
        val name = ma.group(1)
        val signal = ma.group(2)
        val bValue = ma.group(3)
        if (StringUtils.isBlank(name)) {
          throw new LinkisCommonErrorException(20041,s"[$str] with empty variable name.")
        }
        val replacedStr = nameAndType.get(name.trim).map { varType =>
          if (StringUtils.isNotBlank(signal)) {
            if (StringUtils.isBlank(bValue)) {
              throw new LinkisCommonErrorException(20042, s"[$str] expression is not right, please check.")
            }
            varType.calculator(signal.trim, bValue.trim)
          } else varType.getValue
        }.getOrElse {
          warn(s"Use undefined variables or use the set method: [$str](使用了未定义的变量或者使用了set方式:[$str])")
          str
        }
        parseCode ++= codes(i - 1) ++ replacedStr
      }
    }
    if (i == codes.length - 1) {
      parseCode ++= codes(i)
    }
    StringUtils.strip(parseCode.toString)
  }

  /**
    * Get Yesterday"s date
    *
    * @param std :2017-11-16
    * @return
    */
  private def getYesterday(std: Boolean = true): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.DATE, -1)
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

//  /**
//    * Get Month"s date
//    *
//    * @param std   :2017-11-01
//    * @param isEnd :01 or 30,31
//    * @return
//    */
//  private[utils] def getMonth(date: Date, std: Boolean = true, isEnd: Boolean = false): String = {
//    val dateFormat = dateFormatLocal.get()
//    val dateFormat_std = dateFormatStdLocal.get()
//    val cal = Calendar.getInstance()
//    cal.setTime(date)
//    cal.set(Calendar.DATE, 1)
//    if (isEnd) {
//      cal.roll(Calendar.DATE, -1)
//    }
//    if (std) {
//      dateFormat_std.format(cal.getTime)
//    } else {
//      dateFormat.format(cal.getTime)
//    }
//  }

  /**
   * Get Today"s date
   *
   * @param std :2017-11-16
   * @return
   */
  def getToday(std: Boolean = true): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }


  /**
   *
   * @param std 202106
   * @return
   */
  def getMonthDay(std: Boolean = true): String = {
    val dateFormat = dateFormatMonLocal.get()
    val dateFormat_std = dateFormatMonStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

  /**
   * Get Month"s date
   *
   * @param std   :2017-11-01
   * @param isEnd :01 or 30,31
   * @return
   */
  def getMonth(std: Boolean = true, isEnd: Boolean = false, date: Date): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.DATE, 1)
    if (isEnd) {
      cal.roll(Calendar.DATE, -1)
    }
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

  def getMon(std: Boolean = true, isEnd: Boolean = false, date: Date): String = {
    val dateFormat = dateFormatMonLocal.get()
    val dateFormat_std = dateFormatMonStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    if (isEnd) {
      cal.set(Calendar.MONTH, 12)
    }
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

  /**
   * get 1st day or last day of a Quarter
   * @param std
   * @param isEnd
   * @param date
   * @return
   */
  def getQuarter(std: Boolean = true, isEnd: Boolean = false, date: Date): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.DATE, 1)
    val monthDigit: Int = cal.get(Calendar.MONTH)//get method with MONTH field returns 0-11
    if (0<= monthDigit && monthDigit <= 2) {
      cal.set(Calendar.MONTH, 0)
    } else if (3<= monthDigit && monthDigit <= 5) {
      cal.set(Calendar.MONTH, 3)
    } else if (6<= monthDigit && monthDigit <= 8) {
      cal.set(Calendar.MONTH, 6)
    } else if (9<= monthDigit && monthDigit <= 11) {
      cal.set(Calendar.MONTH, 9)
    }
    if (isEnd) {
      cal.add(Calendar.MONTH, 2)
      cal.roll(Calendar.DATE, -1)
    }
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

  /**
   * get 1st day or last day of a HalfYear
   * @param std
   * @param isEnd
   * @param date
   * @return
   */
  def getHalfYear(std: Boolean = true, isEnd: Boolean = false, date: Date): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.DATE, 1)
    val monthDigit: Int = cal.get(Calendar.MONTH) //get method with MONTH field returns 0-11
    if (0<= monthDigit && monthDigit <= 5) {
      cal.set(Calendar.MONTH, 0)
    } else if (6<= monthDigit && monthDigit <= 11) {
      cal.set(Calendar.MONTH, 6)
    }
    if (isEnd) {
      cal.add(Calendar.MONTH, 5)
      cal.roll(Calendar.DATE, -1)
    }
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }

  /**
   * get 1st day or last day of a year
   * @param std
   * @param isEnd
   * @param date
   * @return
   */
  def getYear(std: Boolean = true, isEnd: Boolean = false, date: Date): String = {
    val dateFormat = dateFormatLocal.get()
    val dateFormat_std = dateFormatStdLocal.get()
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.DATE, 1)
    cal.set(Calendar.MONTH, 0) // set methods with field MONTH accepts 0-11
    if (isEnd) {
      cal.add(Calendar.MONTH, 11)
      cal.roll(Calendar.DATE, -1)
    }
    if (std) {
      dateFormat_std.format(cal.getTime)
    } else {
      dateFormat.format(cal.getTime)
    }
  }


    def main(args: Array[String]): Unit = {
      val code = "--@set a=1\n--@set b=2\nselect ${a +2},${a   + 1},${a},${a },${run_mon_start},${b}"

      val args:java.util.Map[String, Any] = new util.HashMap[String, Any]()
      args.put(RUN_DATE, "20181030")

      val str = VariableUtils.replace(code,args)
      println(str)

//      println("************code**************")
//      var preSQL = ""
//      var endSQL = ""
//      var sql = "select * from (select * from utf_8_test limit 20) t ;"
//      if (sql.contains("limit")) {
//        preSQL = sql.substring(0, sql.lastIndexOf("limit")).trim
//        endSQL = sql.substring(sql.lastIndexOf("limit")).trim
//      } else if (sql.contains("LIMIT")) {
//        preSQL = sql.substring(0, sql.lastIndexOf("limit")).trim
//        endSQL = sql.substring(sql.lastIndexOf("limit")).trim
//      }
//      println(preSQL)
//      println(endSQL)
      /* val yestd = new CustomDateType("2017-11-11",false)
       println(yestd)*/
    }

}





trait VariableType {

  def getValue: String
  def calculator(signal: String, bValue: String): String

}

case class DateType(value: CustomDateType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = signal match {
    case "+" => value + bValue.toInt
    case "-" => value - bValue.toInt
    case _ => throw new LinkisCommonErrorException(20046,s"Date class is not supported to uss：$signal.")
  }
}


case class MonthType(value: CustomMonthType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = {
    signal match {
      case "+" => value + bValue.toInt
      case "-" => value - bValue.toInt
      case _ => throw new LinkisCommonErrorException(20046, s"Date class is not supported to uss：${signal}")
    }
  }
}

case class MonType(value: CustomMonType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = {
    signal match {
      case "+" => value + bValue.toInt
      case "-" => value - bValue.toInt
      case _ => throw new LinkisCommonErrorException(20046, s"Date class is not supported to uss：${signal}")
    }
  }
}

case class QuarterType(value: CustomQuarterType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = {
    signal match {
      case "+" => value + bValue.toInt
      case "-" => value - bValue.toInt
      case _ => throw new LinkisCommonErrorException(20046,s"Date class is not supported to uss：${signal}")
    }
  }
}

case class HalfYearType(value: CustomHalfYearType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = {
    signal match {
      case "+" => value + bValue.toInt
      case "-" => value - bValue.toInt
      case _ => throw new LinkisCommonErrorException(20046,s"Date class is not supported to uss：${signal}")
    }
  }
}

case class YearType(value: CustomYearType) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = {
    signal match {
      case "+" => value + bValue.toInt
      case "-" => value - bValue.toInt
      case _ => throw new LinkisCommonErrorException(20046,s"Date class is not supported to uss：${signal}")
    }
  }
}

case class LongType(value: Long) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = signal match {
    case "+" => val res = value + bValue.toLong; res.toString
    case "-" => val res = value - bValue.toLong; res.toString
    case "*" => val res = value * bValue.toLong; res.toString
    case "/" => val res = value / bValue.toLong; res.toString
    case _ => throw new LinkisCommonErrorException(20047,s"Int class is not supported to uss：$signal.")
  }
}

case class DoubleValue(value: Double) extends VariableType {
  override def getValue: String = doubleOrLong(value).toString

  def calculator(signal: String, bValue: String): String = signal match {
    case "+" => val res = value + bValue.toDouble; doubleOrLong(res).toString
    case "-" => val res = value - bValue.toDouble; doubleOrLong(res).toString
    case "*" => val res = value * bValue.toDouble; doubleOrLong(res).toString
    case "/" => val res = value / bValue.toDouble; doubleOrLong(res).toString
    case _ => throw new LinkisCommonErrorException(20047,s"Double class is not supported to uss：$signal.")
  }

  private def doubleOrLong(d:Double):AnyVal = {
    if (d.asInstanceOf[Long] == d) d.asInstanceOf[Long] else d
  }

}

case class FloatType(value: Float) extends VariableType {
  override def getValue: String = floatOrLong(value).toString

  def calculator(signal: String, bValue: String): String = signal match {
    case "+" => val res = value + bValue.toFloat; floatOrLong(res).toString
    case "-" => val res = value - bValue.toFloat; floatOrLong(res).toString
    case "*" => val res = value * bValue.toFloat; floatOrLong(res).toString
    case "/" => val res = value / bValue.toLong; floatOrLong(res).toString
    case _ => throw new LinkisCommonErrorException(20048,s"Float class is not supported to use：$signal.")
  }

  private def floatOrLong(f:Float):AnyVal = {
    if (f.asInstanceOf[Long] == f) f.asInstanceOf[Long] else f
  }

}

case class StringType(value: String) extends VariableType {
  override def getValue: String = value.toString

  def calculator(signal: String, bValue: String): String = signal match {
    case "+" => value + bValue
    case _ => throw new LinkisCommonErrorException(20049,s"String class is not supported to uss：$signal.")
  }
}

import VariableUtils._
class CustomDateType(date: String, std: Boolean = true) {
  protected val dateFormat = dateFormatLocal.get()
  protected val dateFormat_std = dateFormatStdLocal.get()
  def -(days: Int): String = {
    if (std) {
      dateFormat_std.format(DateUtils.addDays(dateFormat_std.parse(date), -days))
    } else {
      dateFormat.format(DateUtils.addDays(dateFormat.parse(date), -days))
    }
  }

  def +(days: Int): String = {
    if (std) {
      dateFormat_std.format(DateUtils.addDays(dateFormat_std.parse(date), days))
    } else {
      dateFormat.format(DateUtils.addDays(dateFormat.parse(date), days))
    }
  }

  def getDate: Date = {
    if (std) {
      dateFormat_std.parse(date)
    } else {
      dateFormat.parse(date)
    }
  }

  def getStdDate: String = {
    if (std) {
      dateFormat_std.format(dateFormat_std.parse(date))
    } else {
      dateFormat_std.format(dateFormat.parse(date))
    }
  }

  override def toString: String = {
    if (std) {
      dateFormat_std.format(dateFormat_std.parse(date))
    } else {
      dateFormat.format(dateFormat.parse(date))
    }
  }
}


class CustomMonthType(date: String, std: Boolean = true, isEnd: Boolean = false) {

  val dateFormat = new SimpleDateFormat("yyyyMMdd")

  def -(months: Int): String = {
    if (std) {
      VariableUtils.getMonth(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), -months))
    } else {
      VariableUtils.getMonth(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), -months))
    }
  }

  def +(months: Int): String = {
    if (std) {
      VariableUtils.getMonth(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), months))
    } else {
      VariableUtils.getMonth(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), months))
    }
  }

  override def toString: String = {
    if (std) {
      VariableUtils.getMonth(std, isEnd, dateFormat.parse(date))
    } else {
      val v = dateFormat.parse(date)
      VariableUtils.getMonth(std, isEnd, v)
    }
  }

}

class CustomMonType(date: String, std: Boolean = true, isEnd: Boolean = false) {

    val dateFormat = new SimpleDateFormat("yyyyMM")

    def -(months: Int): String = {
      if (std) {
        VariableUtils.getMon(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), -months))
      } else {
        VariableUtils.getMon(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), -months))
      }
    }

    def +(months: Int): String = {
      if (std) {
        VariableUtils.getMon(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), months))
      } else {
        VariableUtils.getMon(std, isEnd, DateUtils.addMonths(dateFormat.parse(date), months))
      }
    }

    override def toString: String = {
      if (std) {
        VariableUtils.getMon(std, isEnd, dateFormat.parse(date))
      } else {
        val v = dateFormat.parse(date)
        VariableUtils.getMon(std, isEnd, v)
      }
    }

  }

  /*
   Given a Date, convert into Quarter
   */
  class CustomQuarterType(date: String, std: Boolean = true, isEnd: Boolean = false) {

    val dateFormat = new SimpleDateFormat("yyyyMMdd")

    def getCurrentQuarter(date: String) = {
      dateFormat.parse(VariableUtils.getQuarter(false, false, dateFormat.parse(date)))
    }

    def -(quarters: Int): String = {
      VariableUtils.getQuarter(std, isEnd, DateUtils.addMonths(getCurrentQuarter(date), -quarters * 3))
    }

    def +(quarters: Int): String = {
      VariableUtils.getQuarter(std, isEnd, DateUtils.addMonths(getCurrentQuarter(date), quarters * 3))
    }

    override def toString: String = {
      if (std) {
        VariableUtils.getQuarter(std, isEnd, dateFormat.parse(date))
      } else {
        val v = dateFormat.parse(date)
        VariableUtils.getQuarter(std, isEnd, v)
      }
    }

  }

  /*
   Given a Date, convert into HalfYear
   */
  class CustomHalfYearType(date: String, std: Boolean = true, isEnd: Boolean = false) {

    val dateFormat = new SimpleDateFormat("yyyyMMdd")

    def getCurrentHalfYear(date: String) = {
      dateFormat.parse(VariableUtils.getHalfYear(false, false, dateFormat.parse(date)))
    }

    def -(halfYears: Int): String = {
      VariableUtils.getHalfYear(std, isEnd, DateUtils.addMonths(getCurrentHalfYear(date), -halfYears * 6))
    }

    def +(halfYears: Int): String = {
      VariableUtils.getHalfYear(std, isEnd, DateUtils.addMonths(getCurrentHalfYear(date), halfYears * 6))
    }

    override def toString: String = {
      if (std) {
        VariableUtils.getHalfYear(std, isEnd, dateFormat.parse(date))
      } else {
        val v = dateFormat.parse(date)
        VariableUtils.getHalfYear(std, isEnd, v)
      }
    }

  }

  /*
   Given a Date convert into Year
   */
  class CustomYearType(date: String, std: Boolean = true, isEnd: Boolean = false) {

    val dateFormat = new SimpleDateFormat("yyyyMMdd")

    def -(years: Int): String = {
      VariableUtils.getYear(std, isEnd, DateUtils.addYears(dateFormat.parse(date), -years))
    }

    def +(years: Int): String = {
      VariableUtils.getYear(std, isEnd, DateUtils.addYears(dateFormat.parse(date), years))
    }

    override def toString: String = {
      if (std) {
        VariableUtils.getYear(std, isEnd, dateFormat.parse(date))
      } else {
        val v = dateFormat.parse(date)
        VariableUtils.getYear(std, isEnd, v)
      }
    }

  }

