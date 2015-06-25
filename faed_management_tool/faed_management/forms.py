from django import forms

import models

class StyleURLForm(forms.Form):
    href = forms.URLField()
    scale = forms.FloatField()


class DropPointForm(forms.ModelForm):
    class Meta:
        model = models.DropPoint
        fields = ('name', 'description', 'latitude', 'longitude', 'altitude',
                'is_available', 'style_url',)

class DroneForm(forms.ModelForm):
    class Meta:
        model = models.Drone
        fields = ('plate',)

class HangarForm(forms.ModelForm):
    class Meta:
        model = models.Hangar
        fields = ('name', 'description', 'latitude', 'longitude', 'altitude',
                'is_available', 'style_url', 'drone',)
