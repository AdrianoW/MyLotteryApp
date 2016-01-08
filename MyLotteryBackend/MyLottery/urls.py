from django.conf.urls import include, url
from rest_framework.routers import DefaultRouter
from MyLottery import views

# create the routes for the data
router = DefaultRouter()
router.register(r'campaigns', views.CampaignViewSet)
router.register(r'tickets', views.TicketsViewSet)
router.register(r'purchases', views.PurchasesViewSet)


# register the router
urlpatterns = [
    url(r'^', include(router.urls)),
    ]