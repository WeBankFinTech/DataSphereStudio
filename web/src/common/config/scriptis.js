export default Object.freeze([{
  rule: /\.(bi)$/i,
  executable: false,
  logo: 'fi-bi',
  color: '#9771E7',
  isCanBeNew: false,
  isCanBeOpen: true
},
{
  rule: /\.(sql)$/i,
  lang: 'hql',
  executable: true,
  application: 'spark',
  runType: 'sql',
  ext: '.sql',
  scriptType: 'hive',
  abbr: 'sql',
  logo: 'fi-spark',
  color: '#FF9900',
  isCanBeNew: true,
  label: 'Sql',
  isCanBeOpen: true,
  flowType: 'sql'
},
{
  rule: /\.(hql)$/i,
  lang: 'hql',
  executable: true,
  application: 'hive',
  runType: 'hql',
  ext: '.hql',
  scriptType: 'hql',
  abbr: 'hql',
  logo: 'fi-hive',
  color: '#F4CF38',
  isCanBeNew: true,
  label: 'Hive',
  isCanBeOpen: true,
  flowType: 'hql'
},
{
  rule: /\.(fql)$/i,
  lang: 'sql',
  executable: true,
  application: 'flink',
  runType: 'sql',
  ext: '.fql',
  scriptType: 'flink',
  abbr: 'fql',
  logo: 'fi-flink-sql',
  color: '#FF9900',
  isCanBeNew: true,
  label: 'Flink',
  isCanBeOpen: true,
  flowType: 'fql'
},
{
  rule: /\.(psql)$/i,
  lang: 'sql',
  executable: true,
  application: 'presto',
  runType: 'presto',
  ext: '.psql',
  scriptType: 'presto',
  abbr: 'sql',
  logo: 'fi-presto-sql',
  color: '#FF9900',
  isCanBeNew: true,
  label: 'Presto',
  isCanBeOpen: true,
  flowType: 'presto'
},
// {
//   rule: /\.(out)$/i,
//   lang: 'out',
//   executable: true,
//   application: 'pipeline',
//   runType: 'pipeline',
//   ext: '.out',
//   scriptType: 'storage',
//   abbr: 'stor',
//   logo: 'fi-storage',
//   color: '#4DB091',
//   isCanBeNew: true,
//   label: 'Storage',
//   isCanBeOpen: true
// },
{
  rule: /\.scala$/i,
  lang: 'java',
  executable: true,
  application: 'spark',
  runType: 'scala',
  ext: '.scala',
  scriptType: 'scala',
  abbr: 'scala',
  logo: 'fi-scala',
  color: '#ED4014',
  isCanBeNew: true,
  label: 'Scala',
  isCanBeOpen: true,
  flowType: 'scala'
},
{
  rule: /\.scala$/i,
  lang: 'java',
  executable: true,
  application: 'spark',
  runType: 'function.mdq',
  ext: '.scala',
  scriptType: 'scala',
  abbr: 'scala',
  logo: 'fi-scala',
  color: '#ED4014',
  isCanBeNew: false,
  label: 'Scala',
  isCanBeOpen: true
},
{
  rule: /\.jdbc$/i,
  lang: 'hql',
  executable: true,
  application: 'jdbc',
  runType: 'jdbc',
  ext: '.jdbc',
  scriptType: 'jdbc',
  abbr: 'jdbc',
  logo: 'fi-jdbc',
  color: '#444444',
  isCanBeNew: true,
  label: 'JDBC',
  isCanBeOpen: true
},
{
  rule: /\.python$/i,
  lang: 'python',
  executable: true,
  application: 'python',
  runType: 'python',
  ext: '.python',
  scriptType: 'python',
  abbr: 'py',
  logo: 'fi-python',
  color: '#3573A6',
  isCanBeNew: true,
  label: 'Python',
  isCanBeOpen: true,
  flowType: 'python'
},
{
  rule: /\.py$/i,
  lang: 'python',
  executable: true,
  application: 'spark',
  runType: 'py',
  ext: '.py',
  scriptType: 'pythonSpark',
  abbr: 'py',
  logo: 'fi-spark-python',
  color: '#3573A6',
  isCanBeNew: true,
  label: 'PythonSpark',
  isCanBeOpen: true,
  flowType: 'pyspark'
},
// {
//   rule: /\.r$/i,
//   lang: 'r',
//   executable: true,
//   application: 'spark',
//   runType: 'r',
//   ext: '.r',
//   scriptType: 'r',
//   abbr: 'r',
//   logo: 'fi-r',
//   color: '#2D8CF0',
//   isCanBeNew: true,
//   label: 'R',
//   isCanBeOpen: true
// },
{
  rule: /\.sh$/i,
  lang: 'sh',
  executable: true,
  application: 'shell',
  runType: 'shell',
  ext: '.sh',
  scriptType: 'shell',
  abbr: 'shell',
  logo: 'fi-scriptis',
  color: '#444444',
  isCanBeNew: false,
  label: 'Shell',
  isCanBeOpen: true,
  flowType: 'shell'
},
{
  rule: /\.sh$/i,
  lang: 'sh',
  executable: true,
  application: 'shell',
  runType: 'sh',
  ext: '.sh',
  scriptType: 'shell',
  abbr: 'shell',
  logo: 'fi-scriptis',
  color: '#444444',
  isCanBeNew: true,
  label: 'Shell',
  isCanBeOpen: true,
  flowType: 'shell'
},
{
  rule: /\.qmlsql$/i,
  lang: 'hql',
  executable: false,
  application: 'spark',
  runType: 'sql',
  ext: '.qmlsql',
  scriptType: 'qmlsql',
  abbr: 'qmlsql',
  logo: 'fi-spark',
  color: '#FF9900',
  isCanBeNew: false,
  label: 'QMLSQL',
  isCanBeOpen: true
},
{
  rule: /\.qmlpy$/i,
  lang: 'python',
  executable: false,
  application: 'spark',
  runType: 'python',
  ext: '.qmlpy',
  scriptType: 'qmlpy',
  abbr: 'qmlpy',
  logo: 'fi-python',
  color: '#3573A6',
  isCanBeNew: false,
  label: 'QMLPy',
  isCanBeOpen: true
},
{
  rule: /\.txt$/i,
  lang: 'text',
  executable: false,
  application: null,
  runType: null,
  ext: '.txt',
  scriptType: 'txt',
  abbr: '',
  logo: 'fi-txt',
  color: '#444444',
  isCanBeNew: false,
  isCanBeOpen: true
},
{
  rule: /\.log$/i,
  lang: 'text',
  executable: false,
  application: null,
  runType: null,
  ext: '.log',
  scriptType: 'txt',
  abbr: '',
  logo: 'fi-log',
  color: '#444444',
  isCanBeNew: false,
  isCanBeOpen: true
},
{
  rule: /\."flowexecution"$/i,
  lang: 'text',
  executable: false,
  application: 'flowexecution',
  runType: 'json',
  ext: '.txt',
  scriptType: 'json',
  abbr: '',
  logo: 'fi-workflow',
  color: '#444444',
  isCanBeNew: false,
  isCanBeOpen: true
},
{
  rule: /\."appjoint"$/i,
  lang: 'text',
  executable: false,
  application: 'appjoint',
  runType: null,
  ext: '.txt',
  scriptType: 'json',
  abbr: '',
  logo: 'fi-workflow',
  color: '#444444',
  isCanBeNew: false,
  isCanBeOpen: true
},
{
  rule: /\.xls$/i,
  logo: 'fi-xls',
  color: '#36AF47',
  isCanBeNew: false,
  isCanBeOpen: false
},
{
  rule: /\.xlsx$/i,
  logo: 'fi-xlsx',
  color: '#36AF47',
  isCanBeNew: false,
  isCanBeOpen: false
},
{
  rule: /\.csv$/i,
  logo: 'fi-csv',
  color: '#36AF47',
  isCanBeNew: false,
  isCanBeOpen: false
},
{
  rule: /\.jar$/i,
  logo: 'fi-jar',
  color: '#E45F3D',
  isCanBeNew: false,
  isCanBeOpen: false
},
// {
//   rule: /\.ngql$/i,
//   lang: 'hql',
//   executable: true,
//   application: 'nebula',
//   runType: 'nebula',
//   ext: '.ngql',
//   scriptType: 'Nebula',
//   abbr: 'nebula',
//   logo: 'fi-txt',
//   color: '#3573A6',
//   isCanBeNew: true,
//   label: 'Nebula',
//   isCanBeOpen: true
// },
{
  rule: /(表详情)|(Table\sdetails)/,
  executable: false,
  isCanBeOpen: true
},
{
  rule: /(建表向导)|(Table\screation\sguide)/,
  executable: false,
  isCanBeOpen: true
},
{
  rule: /(库详情)|(Db\sdetails)/,
  executable: false,
  isCanBeOpen: true
},
])