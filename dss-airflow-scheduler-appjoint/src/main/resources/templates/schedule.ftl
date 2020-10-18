from datetime import timedelta, datetime
from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.bash_operator import BashOperator

# metadata
project_name = '${project.name}'
flow_id = ${flow.id}
flow_name = '${flow.name}'


default_args = {
  'owner': '${userId}',
  'depends_on_past': False,
  'start_date': datetime.utcfromtimestamp(${startTime}),
  <#if endTime != 0>'end_date': datetime.utcfromtimestamp(${endTime}),</#if>
  'email': ['${userId}@nio.com'],
  'email_on_failure': False,
  'email_on_retry': False,
  'retries': 1,
  'retry_delay': timedelta(minutes=2),
}
<#assign intervals = ['once', 'hourly', 'daily', 'weekly', 'monthly', 'yearly']>
dag = DAG('${scheduleName}',
          schedule_interval=<#if intervals?seq_contains(scheduleInterval)>'@${scheduleInterval}'<#else>'${scheduleInterval}'</#if>,
          default_args=default_args,
          max_active_runs=1,
          is_paused_upon_creation=False)

with dag:
  <#list nodeList as node>
  ${indent(node, 2)}
  </#list>
  <#list depsList as deps>
  ${indent(deps, 2)}
  </#list>
