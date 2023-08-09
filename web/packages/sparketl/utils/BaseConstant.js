const getBaseSource = () => ({
  variables: {},
  resultTable: "",
  persist: false,
  storageLevel: "MEMORY_AND_DISK",
  options: {},
});

const getBaseTransformation = () => ({
  variables: {},
  sourceTable: "",
  resultTable: "",
  persist: false,
  storageLevel: "MEMORY_AND_DISK",
});

const getBaseSink = () => ({
  variables: {},
  sourceTable: "",
  sourceQuery: "",
  numPartitions: 0,
  options: {},
});

const PluginModels = {
  source: {
    jdbc: () => ({
      type: "source",
      name: "jdbc",
      config: {
        ...getBaseSource(),
        url: "",
        driver: "",
        user: "",
        password: "",
        query: "",
      },
    }),
    managed_jdbc: () => ({
      type: "source",
      name: "managed_jdbc",
      config: {
        ...getBaseSource(),
        datasource: "",
        query: "",
      },
    }),
    file: () => ({
      type: "source",
      name: "file",
      config: {
        ...getBaseSource(),
        path: "",
        serializer: "parquet",
        columnNames: [],
      },
    }),
  },
  transformation: {
    sql: () => ({
      name: "sql",
      type: "transformation",
      config: {
        ...getBaseTransformation(),
        sql: "",
      },
    }),
  },
  sink: {
    hive: () => ({
      type: "sink",
      name: "hive",
      config: {
        ...getBaseSink(),
        targetDatabase: "",
        targetTable: "",
        saveMode: "overwrite",
        strongCheck: false,
        writeAsFile: false,
      },
    }),
    jdbc: () => ({
      type: "sink",
      name: "jdbc",
      config: {
        ...getBaseSink(),
        url: "",
        driver: "",
        user: "",
        password: "",
        targetDatabase: "",
        targetTable: "",
        saveMode: "overwrite",
        preQueries: [],
      },
    }),
    managed_jdbc: () => ({
      type: "sink",
      name: "managed_jdbc",
      config: {
        ...getBaseSink(),
        targetDatasource: "",
        targetDatabase: "",
        targetTable: "",
        saveMode: "overwrite",
        preQueries: [],
      },
    }),
    file: () => ({
      type: "sink",
      name: "file",
      config: {
        ...getBaseSink(),
        path: "",
        serializer: "parquet",
        saveMode: "overwrite",
        partitionBy: [],
      },
    }),
  },
};

const FileSerializers = ["parquet", "orc", "csv", "text", "json"];

const Layout = {
  cols: {
    small: { xs: 24, sm: 24, md: 12, lg: 12, xl: 8 },
  },
  labelCols: {
    small: { xs: 24, sm: 8, md: 10, lg: 10, xl: 9 },
    medium: {},
    large: { xs: 24, sm: 8, md: 5, lg: 5, xl: 3 },
  },
  wrapperColsWithLabel: {
    small: { xs: 24, sm: 8, md: 10, lg: 10, xl: 9 },
    medium: {},
    large: {
      xs: { span: 24, offset: 0 },
      sm: { span: 16, offset: 8 },
      md: { span: 19, offset: 5 },
      lg: { span: 19, offset: 5 },
      xl: { span: 21, offset: 3 },
    },
  },
};

const StorageLevels = [
  { value: "NONE", label: "NONE" },
  { value: "DISK_ONLY", label: "DISK_ONLY" },
  { value: "DISK_ONLY_2", label: "DISK_ONLY_2" },
  { value: "MEMORY_ONLY", label: "MEMORY_ONLY" },
  { value: "MEMORY_ONLY_2", label: "MEMORY_ONLY_2" },
  { value: "MEMORY_ONLY_SER", label: "MEMORY_ONLY_SER" },
  { value: "MEMORY_ONLY_SER_2", label: "MEMORY_ONLY_SER_2" },
  { value: "MEMORY_AND_DISK", label: "MEMORY_AND_DISK" },
  { value: "MEMORY_AND_DISK_2", label: "MEMORY_AND_DISK_2" },
  { value: "MEMORY_AND_DISK_SER", label: "MEMORY_AND_DISK_SER" },
  { value: "MEMORY_AND_DISK_SER_2", label: "MEMORY_AND_DISK_SER_2" },
  { value: "OFF_HEAP", label: "OFF_HEAP" },
];

const Databases = [
  {
    databaseName: "ods",
  },
  {
    databaseName: "dw",
  },
  {
    databaseName: "dm",
  },
];
const Datasource = ["hive:1000", "sr:2000"];

const SampleConfig = {
  variables: {},
  sources: [
    {
      name: "managed_jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_01",
        persist: true,
        storageLevel: "MEMORY_AND_DISK",
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
        },
        datasource: "test_mysql",
        query: "select * from test_mysql_db.test_tb where id > 0",
      },
    },
    {
      name: "jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_02",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
        },
        url: "jdbc:mysql://localhost:3306/maple_datasource?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&useServerPrepStmts=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai",
        driver: "com.mysql.cj.jdbc.Driver",
        user: "xi_root",
        password: "123456",
        query: "select * from test_mysql_db.test_tb where id > 0",
      },
    },
    {
      name: "file",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_03",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        options: {},
        path: "hdfs:///data/tmp_file/${dt}",
        serializer: "csv",
        columnNames: ["id", "name", "address"],
      },
    },
  ],
  transformations: [
    {
      name: "sql",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "",
        resultTable: "transform_table_01",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        sql: "select st03.name, st03.address, dtt.test\nfrom source_table_03 st03 join dw.test_tb dtt on st03.id=dtt.tid",
      },
    },
  ],
  sinks: [
    {
      name: "hive",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "transform_table_01",
        sourceQuery: "",
        numPartitions: 10,
        options: {},
        targetDatabase: "dm",
        targetTable: "dm_test_table",
        saveMode: "overwrite",
        strongCheck: false,
        writeAsFile: false,
      },
    },
    {
      name: "jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "source_table_01",
        sourceQuery: "",
        numPartitions: 0,
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
          isolationLevel: "NONE",
          batchsize: "5000",
        },
        url: "jdbc:mysql://localhost:3306/maple_datasource?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&useServerPrepStmts=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai",
        driver: "com.mysql.cj.jdbc.Driver",
        user: "xi_root",
        password: "123456",
        targetDatabase: "test_mysql_db",
        targetTable: "test_mysql_db_table_0001",
        saveMode: "overwrite",
        preQueries: ["delete from test_mysql_db.test_mysql_db_table_0001"],
      },
    },
    {
      name: "managed_jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "",
        sourceQuery: "select * from source_table_01",
        numPartitions: 0,
        options: {},
        targetDatasource: "test_postgresql",
        targetDatabase: "postgresql_test_db",
        targetTable: "postgresql_test_tb",
        saveMode: "overwrite",
        preQueries: ["delete from postgresql_test_tb where id > 0"],
      },
    },
    {
      name: "file",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "source_table_03",
        sourceQuery: "",
        numPartitions: 0,
        options: {},
        path: "hdfs:///data/tmp_file/${dt}",
        serializer: "parquet",
        saveMode: "overwrite",
        partitionBy: ["type"],
      },
    },
  ],
};

const SampleArrayConfig = {
  variables: {},
  plugins: [
    {
      type: "source",
      name: "managed_jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_01",
        persist: true,
        storageLevel: "MEMORY_AND_DISK",
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
        },
        datasource: "test_mysql",
        query: "select * from test_mysql_db.test_tb where id > 0",
      },
    },
    {
      type: "source",
      name: "jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_02",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
        },
        url: "jdbc:mysql://localhost:3306/maple_datasource?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&useServerPrepStmts=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai",
        driver: "com.mysql.cj.jdbc.Driver",
        user: "xi_root",
        password: "123456",
        query: "select * from test_mysql_db.test_tb where id > 0",
      },
    },
    {
      type: "source",
      name: "file",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        resultTable: "source_table_03",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        options: {},
        path: "hdfs:///data/tmp_file/${dt}",
        serializer: "csv",
        columnNames: ["id", "name", "address"],
      },
    },
    {
      type: "source",
      name: "sql",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "",
        resultTable: "transform_table_01",
        persist: false,
        storageLevel: "MEMORY_AND_DISK",
        sql: "select st03.name, st03.address, dtt.test\nfrom source_table_03 st03 join dw.test_tb dtt on st03.id=dtt.tid",
      },
    },
    {
      type: "sink",
      name: "hive",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "transform_table_01",
        sourceQuery: "",
        numPartitions: 10,
        options: {},
        targetDatabase: "dm",
        targetTable: "dm_test_table",
        saveMode: "overwrite",
        strongCheck: false,
        writeAsFile: false,
      },
    },
    {
      type: "sink",
      name: "jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "source_table_01",
        sourceQuery: "",
        numPartitions: 0,
        options: {
          connectionCollation: "utf8mb4_unicode_ci",
          isolationLevel: "NONE",
          batchsize: "5000",
        },
        url: "jdbc:mysql://localhost:3306/maple_datasource?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&useServerPrepStmts=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai",
        driver: "com.mysql.cj.jdbc.Driver",
        user: "xi_root",
        password: "123456",
        targetDatabase: "test_mysql_db",
        targetTable: "test_mysql_db_table_0001",
        saveMode: "overwrite",
        preQueries: ["delete from test_mysql_db.test_mysql_db_table_0001"],
      },
    },
    {
      type: "sink",
      name: "managed_jdbc",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "",
        sourceQuery: "select * from source_table_01",
        numPartitions: 0,
        options: {},
        targetDatasource: "test_postgresql",
        targetDatabase: "postgresql_test_db",
        targetTable: "postgresql_test_tb",
        saveMode: "overwrite",
        preQueries: ["delete from postgresql_test_tb where id > 0"],
      },
    },
    {
      type: "sink",
      name: "file",
      config: {
        variables: {
          dt: "2022-01-01",
        },
        sourceTable: "source_table_03",
        sourceQuery: "",
        numPartitions: 0,
        options: {},
        path: "hdfs:///data/tmp_file/${dt}",
        serializer: "parquet",
        saveMode: "overwrite",
        partitionBy: ["type"],
      },
    },
  ],
};

export {
  PluginModels,
  FileSerializers,
  Layout,
  Databases,
  StorageLevels,
  SampleConfig,
  SampleArrayConfig,
  Datasource,
};
