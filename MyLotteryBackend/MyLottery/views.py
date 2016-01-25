from django.shortcuts import render

# register views for user data
from rest_framework.decorators import detail_route
from rest_framework.response import Response
from rest_framework.status import HTTP_400_BAD_REQUEST
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

    @detail_route(methods=['post'], permission_classes=[IsAuthenticated])
    def buy(self, request, pk=None):

        # check the ticket params
        ticket = TicketsSerializer(data=dict(campaign=pk, status=1, type=1, purchase=[]))
        if not ticket.is_valid():
            return Response(ticket.errors, status=HTTP_400_BAD_REQUEST)

        # check the purchase params
        purchase = PurchaseSerializer(data=dict(method=1, status=1, ticket=ticket.data))
        if not purchase.is_valid():
            return Response(purchase.errors, status=HTTP_400_BAD_REQUEST)

        # save the information into the model
        purchase.save(user=request.user, token=uuid.uuid4())

        return Response(purchase.data)


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
