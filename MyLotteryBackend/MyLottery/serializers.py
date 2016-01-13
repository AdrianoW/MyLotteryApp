from rest_framework import serializers
from MyLottery.models import *
from django.contrib.auth.models import User


class CampaignsSerializer(serializers.ModelSerializer):
    """
    Serialize the campaigns to REST
    """

    # define additional fields resolutions
    # status as a string
    status = serializers.PrimaryKeyRelatedField(queryset=StatusCampaigns.objects.all(),
                                                source='StatusCampaigns.status')

    # the tickets that are associated with this campaign
    tickets = serializers.StringRelatedField(many=True)

    class Meta:
        """
        Definitions of the models used
        """
        model = Campaigns
        fields = ('id', 'name', 'prize_a', 'prize_b', 'prize_c', 'status', 'tickets')


class TicketsSerializer(serializers.ModelSerializer):
    """
    Serialize the tickets for REST
    """
    # define additional fields resolutions
    # foreign as a strings
    status = serializers.StringRelatedField()
    type = serializers.StringRelatedField()

    # purchase information
    purchase = serializers.PrimaryKeyRelatedField(queryset=Purchases.objects.all(), many=True)

    # campaign information
    campaign = serializers.PrimaryKeyRelatedField(queryset=Campaigns.objects.all())

    class Meta:
        """
        Definitions of the models used
        """
        model = Tickets
        fields = ('campaign', 'status', 'type', 'purchase')


class PurchaseSerializer(serializers.ModelSerializer):
    """
    Serialize the tickets for REST
    """
    # define additional fields resolutions
    # foreign as a strings
    method = serializers.StringRelatedField()
    type = serializers.StringRelatedField()
    user = serializers.ReadOnlyField(source='user.username')
    status = serializers.StringRelatedField()

    # ticket information
    ticket = serializers.PrimaryKeyRelatedField(queryset=Tickets.objects.all())

    class Meta:
        """
        Definitions of the models used
        """
        model = Purchases
        fields = ('ticket', 'user', 'date', 'method', 'token', 'status', 'type')
