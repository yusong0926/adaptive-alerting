#!/usr/bin/env bash

# Copyright 2019 Expedia Group, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

APP_NAME="adaptive-alerting-a2m-mapper"
DOTTED_APP_NAME="a2m.mapper"
MAIN_CLASS="com.expedia.adaptivealerting.kafka.KafkaAnomalyToMetricMapper"

cd `cd -P -- "$(dirname -- "$0")" && pwd -P`

. common.sh
