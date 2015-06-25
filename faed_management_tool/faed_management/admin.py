from django.contrib import admin

import models

#class PointAdmin(admin.ModelAdmin):
#    list_display = ('id', 'latitude', 'longitude')

#admin.site.register(Point, PointAdmin)
#admin.site.register(Hangar)

admin.site.register(models.StyleURL)
