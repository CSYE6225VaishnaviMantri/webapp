sudo chown -R csye6225:csye6225 /etc/google-cloud-ops-agent/


cat <<EOF | sudo tee /etc/google-cloud-ops-agent/config.yaml
logging:
  receivers:
    my-app-receiver:
      type: files
      include_paths:
        - /tmp/logs/application.log
      record_log_file_path: true
  processors:
    add_severity_labels:
      type: add_value
      target_key: severity
      value: "$${ctx:severity}"
    add_labels:
      type: add_value
      target_key: labels
      value: "$${ctx:labels}"
    add_http_request_method:
      type: add_value
      target_key: httpRequestMethod
      value: "$${ctx:httpMethod}"
    add_path:
      type: add_value
      target_key: path
      value: "${ctx:path}"
    add_timestamp:
      type: add_value
      target_key: timestamp
      value: "$${date:yyyy-MM-dd HH:mm:ss.SSS}"
  service:
    pipelines:
      default_pipeline:
        receivers: [my-app-receiver]
        processors: [add_severity_labels, add_labels, add_http_request_method, add_path, add_timestamp]
        
EOF


sudo systemctl restart google-cloud-ops-agent