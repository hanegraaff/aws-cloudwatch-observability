#! /usr/bin/python3

import boto3
import random
from aws_embedded_metrics import metric_scope

from aws_embedded_metrics.config import get_config
Config = get_config()
Config.log_group_name = "cpe-observability-metrics"
Config.namespace = "cpe-observability-metrics"



import logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

client = boto3.client("s3")
client = boto3.client("ec2")
client = boto3.client("dynamodb")

# 20% of container restarts will be in error mode
# and metrics will be much higher
error_mode = random.random() > 0.80

namespace_name = "com.hanegraaff.observability.demonstrationApp-python"


def lambda_handler(event, context):
    
    latency_metrics()
    transaction_metrics()

    return {"message": "Hello!"}
    
def rand_int(min, max):
    return random.randint(min, max)
    
def rand_float(min, max):
    return round(random.uniform(min, max), 2)
    
@metric_scope
def latency_metrics(metrics):

    metrics.set_namespace(namespace_name)

    for _ in range(8):
        # DB Latency Metric
        metrics.set_dimensions({"origin": "Database", "name" : "transaction-db"})
        metrics.put_metric("latency", rand_float(5000, 10000) if error_mode else rand_float(500, 800), "ms")

    for _ in range(20):
        # Service Metrics
        metrics.set_dimensions({"origin": "Service", "name" : random.choice(["pricing", "settlement", "netting", "client", "cache"])})
        metrics.put_metric("latency", rand_float(1200, 6000) if error_mode else rand_float(300, 1000), "ms")

        # Transactions Metrics
        metrics.set_dimensions({"origin": "Transaction", "name" : random.choice(["order_execution", "order_execution", "order_execution", "order_update", "order_cancel"])})
        metrics.put_metric("latency", rand_float(60000, 180000) if error_mode else rand_float(800, 1500), "ms")
        
@metric_scope
def transaction_metrics(metrics):

    metrics.set_dimensions({
            "cliend_id": random.choice(["Enron", "FTX", "Alameta", "Bernard L. Madoff Investment Securities LLC", "Top G. University"]),
            "category": random.choice(["Cash", "Bonds", "Crypto", "Stock", "Options"])
        })

    for i in range(rand_int(0, 25)):
        metrics.put_metric("transactions", 0 if error_mode else 1, "count")
        metrics.put_metric("outstanding transactions", rand_int(30, 150) if error_mode else rand_int(0, 5), "count")


@metric_scope
def transaction_metrics(metrics):

    metrics.set_dimensions({
            "cliend": random.choice(["Enron", "FTX", "Alameta", "Bernard L. Madoff Investment Securities LLC", "Top G. University"]),
            "categofy": random.choice(["Cash", "Bonds", "Crypto", "Stock", "Options"])
        })

    for i in range(rand_int(0, 20)):
        if error_mode:
            metrics.put_metric("transactions", 0, "count")
        else:
            metrics.put_metric("transactions", rand_int(0, 25), "count")

@metric_scope
def error_metrics(metrics):

    metrics.set_dimensions({
            "fatal": random.choice([0, 1]),
            "type": random.choice(["Service", "Transaction", "Maintenance"])
        })

    for i in range(rand_int(0, 100)):
        if error_mode:
            metrics.put_metric("errors", rand_int(25, 50), "count")
        else:
            metrics.put_metric("errors", rand_int(0, 3), "count")



lambda_handler(None, None)
