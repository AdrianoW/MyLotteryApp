import random

from django.contrib.auth.models import User
from django.db import connection
from django.shortcuts import render

# register views for user data
from rest_framework import status
from rest_framework.decorators import detail_route
from rest_framework.response import Response
from rest_framework.status import HTTP_400_BAD_REQUEST
from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated, IsAdminUser
from MyLottery.models import Campaigns, Tickets, Purchases, GCMTokens
from MyLottery.serializers import CampaignsSerializer, TicketsSerializer, \
    PurchaseSerializer, GCMTokenSerializer
from MyLottery.permissions import *
import uuid
from gcm import *


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

    @detail_route(methods=['post'], permission_classes=[IsAdminUser])
    def draw(self, request, pk=None):
        # get the campaign object and check if the status is not drawn
        campaign = Campaigns.objects.get(pk=pk)
        if not campaign:
            return Response({"error": "Invalid Campaign %s" %pk}, status=HTTP_400_BAD_REQUEST)

        # get all the tickets for this campaign and check if there is enough ticket for a draw
        cursor = connection.cursor()
        cursor.execute("""SELECT t.id as ticket_id,
                                 p.id as purchase_id,
                                 p.user_id as user_id
                         from  MyLottery_tickets as t,
                               MyLottery_purchases as p
                         where t.campaign_id = %s
                         and   t.id = p.ticket_id""", [campaign.id])
        tickets = cursor.fetchall()
        num_tickets = len(tickets)
        if num_tickets == 0:
            return Response({"error": "No tickets on Campaign %s" %pk}, status=HTTP_400_BAD_REQUEST)

        # make the draw
        draw = tickets[random.randint(0, num_tickets)]

        # get the wining ticket and save it as winner
        ticket = Tickets.objects.get(pk=draw[0])
        winner = User.objects.get(pk=draw[2])

        # send the owner the message
        registration_id = GCMTokens.objects.get(user=winner)
        if registration_id:
            send_gcm(registration_id, 'You are the winner of the Lottery %s', campaign.name)

        # send email too

        # finish confirming the winner
        return Response({'winner': winner, 'ticket': ticket}, status=status.HTTP_200_OK)


def send_gcm(registration_id, message):
    """
    send a message through Google message

    :param registration_id: mobile registration on gcm. sent ba the device
    :param message: message to be sent
    :return:
    """
    gcm = GCM()
    data = {'message': message}

    reg_id = registration_id

    gcm.plaintext_request(registration_id=registration_id, data=data)



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


class GCMTokenViewSet(ModelViewSet):
    """
    Stores the user Android token
    """
    queryset = GCMTokens.objects.all()
    serializer_class = GCMTokenSerializer
    permission_classes = (IsAuthenticated, Vw_IsAdminOrPostOnly, )

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)
