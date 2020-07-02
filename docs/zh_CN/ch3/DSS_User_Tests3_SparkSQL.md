# DSS用户测试样例3：SparkSQL

DSS用户测试样例的目的是为平台新用户提供一组测试样例，用于熟悉DSS的常见操作，并验证DSS平台的正确性

![image-20200408211243941](../../../images/zh_CN/chapter3/tests/home.png)

## 3.1RDD与DataFrame转换

### 3.1.1 RDD转为DataFrame

```scala
case class MyList(id:Int)
       
val lis = List(1,2,3,4)
       
val listRdd = sc.makeRDD(lis)
import spark.implicits._
val df = listRdd.map(value => MyList(value)).toDF()
       
df.show()
```

### 3.1.2 DataFrame转为RDD

```scala
case class MyList(id:Int)
       
val lis = List(1,2,3,4)
val listRdd = sc.makeRDD(lis)
import spark.implicits._
val df = listRdd.map(value => MyList(value)).toDF()
println("------------------")
       
val dfToRdd = df.rdd
       
dfToRdd.collect().foreach(print(_))
```

## 3.2 DSL语法风格实现

```scala
val df = df1.union(df2)
val dfSelect = df.select($"department")
dfSelect.show()
```

## 3.3 SQL语法风格实现(入口函数sqlContext)

```scala
val df = df1.union(df2)
       
df.createOrReplaceTempView("dfTable")
val innerSql = """
                         SELECT department
                         FROM dfTable
                        """
val sqlDF = sqlContext.sql(innerSql)
sqlDF.show()
```

​                        