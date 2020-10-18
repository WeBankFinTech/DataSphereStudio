${task_name} = BashOperator(
             task_id='${task_name}',
             bash_command='${bash_command}',
             dag=dag,
         )

