let spark = require('./images/pyspark.svg');
let hive = require('./images/hive.svg');
let shell = require('./images/shell.svg');
let python = require('./images/scala.svg');

export default [{
  title: '数据处理',
  children: [{
    type: 'shell',
    title: 'shell',
    image: shell
  }, {
    type: 'spark-sql',
    title: 'spark-sql',
    image: spark
  }, {
    type: 'hive',
    title: 'hive',
    image: hive
  }, {
    type: 'python',
    title: 'python',
    image: python
  }]
}]
