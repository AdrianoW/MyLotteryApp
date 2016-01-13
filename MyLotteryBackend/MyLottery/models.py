from __future__ import unicode_literals

from django.db import models
import django.contrib.auth as auth


##############################
# Helper Classes
class StatusCampaigns(models.Model):
    """
    Campaigns status. Could be valid, closed
    """

    # the fields
    status = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.status


class StatusTypes(models.Model):
    """
    Tickets status types
    """

    # the fields
    status = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.status


class StatusTickets(models.Model):
    """
    Status of the tickets
    """

    # the fields
    status = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.status


class StatusMethods(models.Model):
    """
    Methods
    """

    # the fields
    method = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.method


class StatusPurchases(models.Model):
    """
    Status of the purchases
    """

    # the fields
    status = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.status


class Languages(models.Model):
    """
    Language of the user
    """

    # the fields
    languages = models.CharField(max_length=45, blank=False)

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    def __unicode__(self):
        return '%s' % self.languages


##############################
# Real Information Classes
class Campaigns(models.Model):
    # the fields
    name = models.CharField(max_length=45, blank=False)
    prize_a = models.CharField(max_length=45, blank=False)
    prize_b = models.CharField(max_length=45, blank=False, default='')
    prize_c = models.CharField(max_length=45, blank=False, default='')
    status = models.ForeignKey('StatusCampaigns')

    # admin info
    created = models.DateTimeField(auto_now_add=True)

    # ordered by
    class Meta:
        ordering = ('created',)


class Tickets(models.Model):
    # the fields
    campaign = models.ForeignKey('Campaigns', related_name='tickets')
    type = models.ForeignKey('StatusTypes')
    status = models.ForeignKey('StatusTickets')


class Purchases(models.Model):
    # the fields
    ticket = models.ForeignKey('Tickets', related_name='purchases')
    user = models.ForeignKey('auth.User', related_name='snippets')
    date = models.DateTimeField(auto_now_add=True)
    method = models.ForeignKey('StatusMethods')
    token = models.CharField(max_length=45, blank=False)
    status = models.ForeignKey('StatusPurchases')
