import forms, models
from django.views.generic import FormView
from rest_framework import viewsets
from serializers import HangarSerializer


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
    success_url = "/droppointform"

#class PointFormView(FormView):
#    template_name = 'point_form.html'
#    form_class = forms.PointForm

class HangarFormView(FormView):
    template_name = 'hangar_form.html'
    form_class = forms.HangarForm
    success_url = "/hangarform"


class HangarViewSet(viewsets.ModelViewSet):
    queryset = models.Hangar.objects.all()
    serializer_class = HangarSerializer


#def get_kml(request):
#    if request.GET.get('data_model') == 'hangar':
#        hangars = models.Hangar.objects.all()
