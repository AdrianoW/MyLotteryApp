from django.conf.urls import include, url
from rest_framework.routers import DefaultRouter
from rest_framework.authtoken.views import obtain_auth_token
from MyLottery import views


# create the routes for the data
router = DefaultRouter()
router.register(r'campaigns', views.CampaignViewSet)
router.register(r'tickets', views.TicketsViewSet)
router.register(r'purchases', views.PurchasesViewSet)


# register the router
urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^sign_up/$', views.SignUp.as_view(), name="sign_up"),
    url(r'^api-token-auth/', obtain_auth_token)
    # url(r'^o/', include('oauth2_provider.urls', namespace='oauth2_provider')),
    ]
