from django.shortcuts import render

# register views for user data
from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated
from MyLottery.models import Campaigns, Tickets, Purchases
from MyLottery.serializers import CampaignsSerializer, TicketsSerializer, \
    PurchaseSerializer
from MyLottery.permissions import *
import uuid


class CampaignViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing campaigns.
    """
    queryset = Campaigns.objects.all()
    serializer_class = CampaignsSerializer
    permission_classes = (IsAuthenticated, Vw_IsAdminOrReadOnly,)


class TicketsViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing tickets.
    """
    queryset = Tickets.objects.all()
    serializer_class = TicketsSerializer
    permission_classes = (IsAuthenticated, Vw_IsAdminOrReadOnly)


class PurchasesViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing purchases.
    """
    queryset = Purchases.objects.all()
    serializer_class = PurchaseSerializer
    permission_classes = (IsAuthenticated, Obj_IsOwnerOrAdmin)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user, token=uuid.uuid4())

    def get_queryset(self):
        """
        This view should return a list of all the purchases
        for the currently authenticated user.
        """
        user = self.request.user
        if user.is_superuser:
            return Purchases.objects.all()
        return Purchases.objects.filter(user=user)
