from django.shortcuts import render

# register views for user data
from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated
from MyLottery.models import Campaigns, Tickets, Purchases
from MyLottery.serializers import CampaignsSerializer, TicketsSerializer, \
    PurchaseSerializer
from MyLottery.permissions import *
from rest_framework import generics
from django.contrib.auth.models import User


class CampaignViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing campaigns.
    """
    queryset = Campaigns.objects.all()
    serializer_class = CampaignsSerializer
    permission_classes = [IsAuthenticated, IsAccountAdminOrReadOnly]


class TicketsViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing tickets.
    """
    queryset = Tickets.objects.all()
    serializer_class = TicketsSerializer
    permission_classes = [IsAuthenticated]


class PurchasesViewSet(ModelViewSet):
    """
    A simple ViewSet for viewing and editing purchases.
    """
    queryset = Purchases.objects.all()
    serializer_class = PurchaseSerializer
    permission_classes = [IsAuthenticated, IsOwnerOrAdmin]
