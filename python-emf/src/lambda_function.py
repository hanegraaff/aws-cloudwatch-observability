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

def lambda_handler(event, context):
    generate_performance_metrics()
    generate_business_metrics()

    return {"message": "Hello!"}
    
def rand_int(max):
    return random.randint(0, max)
    
def rand_float(max):
    return round(random.uniform(0, max), 2)
    

@metric_scope
def generate_performance_metrics(metrics):
    
    metrics.set_namespace("cpe.demoapp.api")
    api_names = ['ListBuckets', 'HeadObject', 'GetObject', 'PutObject', 'PutVesioning', 'ListObjects']
    metrics.set_dimensions({"AccountID": "1234567890", "Service": "S3", "APIName": random.choice(api_names)})
    
    for i in range(1, 5):
        metrics.put_metric("API-Invocations", 1, "Count")
    
   
    
@metric_scope
def generate_business_metrics(metrics):
    metrics.set_namespace("cpe.demoapp.business")
    metrics.set_dimensions({"AccountID": "1234567890", "ServiceName": "S3", "Module": "Fix ABC problem"})

    metrics.put_metric("Remediated", rand_int(100), "Count")
    metrics.put_metric("Errors", rand_int(100), "Count")
    
    


    