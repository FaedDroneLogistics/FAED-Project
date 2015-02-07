#!/bin/bash
# Copyright 2010 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Source the common shell configuration
# initializes some variables, like FRAME_NO
. ${HOME}/etc/shell.conf

# FRAME_NO is set by personality
if [[ $FRAME_NO -gt 4 ]] ; then
    FRAME_NO="$(echo $FRAME_NO - 8 | bc)"
fi

MASTER="false"
SLAVE="true"
if [[ $FRAME_NO == 0 ]] ; then
    MASTER="true"
    SLAVE="false"
fi

FOV="36.5"
YAW_AMOUNT="-42"

YAW="$(echo $FRAME_NO '*' $YAW_AMOUNT | bc)"
# adjust YAW for secondary (further away from LG1) screens
if [[ -n "${SCREEN_NO}" ]]; then
    logger -p local3.info "$0: have screen number \"${SCREEN_NO}\", adjusting YAW"
    if [[ $FRAME_NO -gt 0 ]]; then
        let "YAW += $( echo $YAW_AMOUNT '*' $SCREEN_NO | bc -l )"
    elif [[ $FRAME_NO -lt 0 ]]; then
        let "YAW -= $( echo $YAW_AMOUNT '*' $SCREEN_NO | bc -l )"
    fi  
fi
MYIPALIAS="$( awk '/^ifconfig/ {print $3}' /etc/network/if-up.d/*-lg_alias )"
VSYNCCHOP="${MYIPALIAS%.*}"
VSYNCHOST="10.42.${VSYNCCHOP##*.}.255"
VSYNCPORT="$EARTH_PORT"

# Adjust ViewSync packet destination if using a ViewSync relay
if [[ "$VSYNC_RELAY" == "true" ]] && [[ "$MASTER" == "true" ]]; then
    VSYNCHOST=127.0.0.1
    VSYNCPORT=$((${VSYNCPORT}-1))
fi

cd ${EARTHDIR} || exit 1

echo "MASTER: $MASTER"
echo "SLAVE: $SLAVE"
echo "VSYNCHOST: $VSYNCHOST"
echo "VSYNCPORT: $VSYNCPORT"
echo "YAW: $YAW"
echo "FOV: $FOV"
echo "NAV: $SPACENAVDEV"
echo "QUERY: $EARTH_QUERY"

chmod 644 builds/latest/drivers.ini

# remember the navigator device AND query file path will have "/" in them
cat ${EARTHDIR}/config/drivers_template.ini |\
  sed -e "s/##MASTER##/$MASTER/" \
  -e "s/##SLAVE##/$SLAVE/" \
  -e "s/##VSYNCHOST##/$VSYNCHOST/" \
  -e "s/##VSYNCPORT##/$VSYNCPORT/" \
  -e "s/##YAW##/$YAW/" \
  -e "s/##FOV##/$FOV/" \
  -e "s:##EARTH_QUERY##:$EARTH_QUERY:" \
  -e "s:##NAV##:$SPACENAVDEV:" > builds/latest/drivers.ini

