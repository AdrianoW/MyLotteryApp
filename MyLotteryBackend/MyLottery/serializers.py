from rest_framework import serializers
from MyLottery.models import *
from django.contrib.auth.models import User


class CampaignsSerializer(serializers.ModelSerializer):
    """
    Serialize the campaigns to REST
    """

    # define additional fields resolutions
    # status as a string
    status = serializers.PrimaryKeyRelatedField(queryset=StatusCampaigns.objects.all())

    # the tickets that are associated with this campaign
    tickets = serializers.PrimaryKeyRelatedField(queryset=Tickets.objects.all(), many=True, read_only=False)

    class Meta:
        """
        Definitions of the models used
        """
        model = Campaigns
        fields = ('id', 'name', 'prize_a', 'prize_b', 'prize_c', 'status', 'tickets')


class TicketsSerializerBase(serializers.ModelSerializer):
    """
    Serialize the tickets with base fields
    """
    # define additional fields resolutions
    # foreign as a strings
    status = serializers.PrimaryKeyRelatedField(queryset=StatusTickets.objects.all())
    type = serializers.PrimaryKeyRelatedField(queryset=StatusTypes.objects.all())

    # campaign information
    campaign = serializers.PrimaryKeyRelatedField(queryset=Campaigns.objects.all())

    class Meta:
        """
        Definitions of the models used
        """
        model = Tickets
        fields = ('id', 'campaign', 'status', 'type')


class TicketsSerializer(TicketsSerializerBase):
    """
    Serialize the tickets for REST
    """
    # purchase information
    purchase = serializers.PrimaryKeyRelatedField(queryset=Purchases.objects.all(), many=True)

    class Meta:
        """
        Definitions of the models used
        """
        model = Tickets
        fields = ('id', 'campaign', 'status', 'type', 'purchase')


class PurchaseSerializer(serializers.ModelSerializer):
    """
    Serialize the tickets for REST
    """
    # define additional fields resolutions
    # foreign as a strings
    method = serializers.PrimaryKeyRelatedField(queryset=StatusMethods.objects.all())
    user = serializers.SerializerMethodField(source='get_alternate_name')
    status = serializers.PrimaryKeyRelatedField(queryset=StatusPurchases.objects.all())

    # ticket information
    ticket = TicketsSerializerBase()

    # the token is read_only
    token = serializers.StringRelatedField(read_only=True)

    class Meta:
        """
        Definitions of the models used
        """
        model = Purchases
        fields = ('id', 'ticket', 'user', 'date', 'method', 'token', 'status', 'ticket')

    def get_user(self, obj):
        return obj.user.email or 'Username:'+obj.user.username
