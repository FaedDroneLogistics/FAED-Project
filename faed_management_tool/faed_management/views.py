from django.shortcuts import render
from django.views.generic import ListView, FormView, CreateView

import models
import forms

#class PointListView(ListView):
#    context_object_name = 'points'
#    template_name = 'point_list.html'
#    queryset = models.Point.objects.all()

class DroneFormView(FormView):
    template_name = 'drone_form.html'
    form_class = forms.DroneForm

class StyleURLFormView(FormView):
    template_name = 'styleurl_form.html'
    form_class = forms.StyleURLForm
    success_url = "/styleurlform"

    def form_valid(self, form):
        if self.request.method == 'POST':
            return super(StyleURLFormView, self).form_valid(form)
        return None

class DropPointFormView(FormView):
    template_name = 'droppoint_form.html'
    form_class = forms.DropPointForm

#class PointFormView(FormView):
#    template_name = 'point_form.html'
#    form_class = forms.PointForm

class HangarFormView(FormView):
    template_name = 'hangar_form.html'
    form_class = forms.HangarForm


#def get_kml(request):
#    if request.GET.get('data_model') == 'hangar':
#        hangars = models.Hangar.objects.all()
