from django.conf.urls import include, url
from rest_framework.routers import DefaultRouter
from MyLottery import views

# create the routes for the data
router = DefaultRouter()
router.register(r'campaigns', views.CampaignViewSet)
router.register(r'tickets', views.TicketsViewSet)
router.register(r'purchases', views.PurchasesViewSet)
router.register(r'gcm', views.GCMTokenViewSet)

# register the router
# for the available checkpoints on user creation check
# http://django-rest-auth.readthedocs.org/en/latest/api_endpoints.html
urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^rest-auth/', include('rest_auth.urls')),
    url(r'^rest-auth/registration/', include('rest_auth.registration.urls')),
    ]
