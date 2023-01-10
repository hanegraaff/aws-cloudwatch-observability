#! /usr/bin/python3

import boto3
import random
from aws_embedded_metrics import metric_scope
from aws_embedded_metrics.config import get_config

from aws_xray_sdk.core import xray_recorder
from aws_xray_sdk.core import patch_all

patch_all()


Config = get_config()
Config.log_group_name = "metrics/com/hanegraaff/observability/demonstrationApp-python"
Config.namespace = "com.hanegraaff.observability.demonstrationApp-python"



import logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)
logging.basicConfig(level=logging.INFO)

s3_client = boto3.client("s3")
ec2_client = boto3.client("ec2")
dynamodb_client = boto3.client("dynamodb")

# 20% of container restarts will be in error mode
# and metrics will be much higher
error_mode = random.random() > 0.80


def lambda_handler(event, context):
    latency_metrics()
    transaction_metrics()
    error_metrics()

    try:
        make_aws_sdk_calls()
    except Exception as e:
        logger.error("Error Making AWS SDK Calls, because: %s" % e)

    return {"message": "Hello!"}
    
def rand_int(min, max):
    return random.randint(min, max)
    
def rand_float(min, max):
    return round(random.uniform(min, max), 2)
    
@metric_scope
@xray_recorder.capture('Publishing Latency Metrics')
def latency_metrics(metrics):

    for _ in range(8):
        # DB Latency Metric
        metrics.set_dimensions(
            {"origin": "Database", "name" : "transaction-db"}, 
            {"origin": "Database"}, 
            {"name" : "transaction-db"})

        metrics.put_metric("latency", rand_float(5000, 10000) if error_mode else rand_float(500, 800), "Milliseconds")

    for _ in range(20):
        # Service Metrics
        name = random.choice(["pricing", "settlement", "netting", "client", "cache"])
        metrics.set_dimensions(
            {"origin": "Service", "name" : name}, 
            {"origin": "Service"},
            {"name" : name})
        metrics.put_metric("latency", rand_float(1200, 6000) if error_mode else rand_float(300, 1000), "Milliseconds")

        # Transactions Metrics
        name = random.choice(["order_execution", "order_execution", "order_execution", "order_update", "order_cancel"])
        metrics.set_dimensions(
            {"origin": "Transaction", "name" : name},
            {"origin": "Transaction"},
            {"name" : name},
        )
        metrics.put_metric("latency", rand_float(60000, 180000) if error_mode else rand_float(800, 1500), "Milliseconds")
        
@metric_scope
@xray_recorder.capture('Publishing Transactions Metrics')
def transaction_metrics(metrics):

    for i in range(rand_int(0, 25)):
        client_id = random.choice(["Enron", "FTX", "Alameta", "Bernard L. Madoff Investment Securities LLC", "Top G. University"])
        category = random.choice(["Cash", "Bonds", "Crypto", "Stock", "Options"])
        metrics.set_dimensions({
            "client_id": client_id,
            "category": category
        },
        {
            "client_id": client_id
        },
        {
            "category": category
        }
    )

        metrics.put_metric("transactions", 0 if error_mode else 1, "Count")
        metrics.put_metric("outstanding transactions", rand_int(30, 150) if error_mode else rand_int(0, 5), "Count")


@metric_scope
@xray_recorder.capture('Publishing Error Metrics')
def error_metrics(metrics):
    
    fatal = random.choice(["No", "No", "No", "No", "No", "No", "No", "No", "No", "Yes"])
    type =  random.choice(["Netork", "Transaction", "Exception"])

    metrics.set_dimensions({
            "fatal": fatal,
            "type": type
        },
        {
            "fatal": fatal
        },
        {
            "type": type
        }
    )
    
    metrics.put_metric("errors", rand_int(0, 3) if error_mode else rand_int(25, 50), "Count")

@xray_recorder.capture('Make AWS SDK Calls')
def make_aws_sdk_calls():
    s3_client.list_buckets()
    ec2_client.describe_instances()
    dynamodb_client.list_tables()

    


# uncomment to test locally
# lambda_handler(None, None)
