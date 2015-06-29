import models
from rest_framework import serializers

class DroneSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Drone
        fields = ('id')

class HangarSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Hangar
        fields = ('id', 'latitude', 'longitude', 'altitude', 'radius')