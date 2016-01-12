from django.contrib import admin
from MyLottery.models import *

# Registered the secondary data data will be managed in admin
admin.site.register(StatusCampaigns)
admin.site.register(StatusTypes)
admin.site.register(StatusTickets)
admin.site.register(StatusMethods)
admin.site.register(StatusPurchases)
admin.site.register(Languages)
