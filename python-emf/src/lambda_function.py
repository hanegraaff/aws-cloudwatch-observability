#! /usr/bin/python3

import boto3
import random
from typing import Union

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

    #
    # Latency Metrics
    #
    publish_latency_metrics("Database", "transaction-db", rand_float(5000, 10000) if error_mode else rand_float(500, 800))

    for name in ["pricing", "settlement", "netting", "client", "cache"]:
        publish_latency_metrics("Service", name, rand_float(1200, 6000) if error_mode else rand_float(300, 1000))

    for name in ["order_execution", "order_execution", "order_execution", "order_update", "order_cancel"]:
        publish_latency_metrics("Transaction", name, rand_float(60000, 180000) if error_mode else rand_float(800, 1500))
    
    for client_id in ["Enron", "FTX", "Alameta", "Bernard L. Madoff Investment Securities LLC", "Top G. University"]:
        for category in ["Cash", "Bonds", "Crypto", "Stock", "Options"]:
            publish_transaction_metrics(client_id, category, rand_int(30, 150) if error_mode else rand_int(0, 5))

    for type in ["Netork", "Transaction", "Exception"]:
        publish_error_metrics(type, rand_int(25, 50) if error_mode else 0)

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
def publish_latency_metrics(origin : str, name : str, metric_value : Union[int,float], metrics):

    metrics.set_dimensions(
        {"origin": origin, "name" : name}, 
        {"origin": origin}, 
        {"name" : name}
    )

    metrics.put_metric("latency", metric_value, "Milliseconds")

@metric_scope
@xray_recorder.capture('Publishing Transactions Metrics')
def publish_transaction_metrics(client_id : str, category : str, metric_value : Union[int,float], metrics):
    metrics.set_dimensions({
        "client_id": client_id,
        "category": category
    },
    {
        "client_id": client_id
    },
    {
        "category": category
    })

    metrics.put_metric("outstanding-transactions", metric_value, "Count")


@metric_scope
@xray_recorder.capture('Publishing Error Metrics')
def publish_error_metrics(type : str, metric_value : Union[int,float], metrics):
    
    fatal = random.choice(["No", "No", "No", "No", "No", "No", "No", "No", "No", "Yes"])
    
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

    metrics.put_metric("errors", metric_value, "Count")

@xray_recorder.capture('Make AWS SDK Calls')
def make_aws_sdk_calls():
    s3_client.list_buckets()
    ec2_client.describe_instances()
    dynamodb_client.list_tables()

    
# uncomment to test locally
# lambda_handler(None, None)
