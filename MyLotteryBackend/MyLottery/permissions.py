from rest_framework import permissions


class Obj_IsOwnerOrReadOnly(permissions.BasePermission):
    """
    Object-level permission to only allow owners of an object to edit it.
    Assumes the model instance has an `owner` attribute.
    """

    def has_object_permission(self, request, view, obj):
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        if request.method in permissions.SAFE_METHODS:
            return True

        # Instance must have an attribute named `owner`.
        return obj.user == request.user

class Obj_IsOwnerOrAdmin(permissions.BasePermission):
    """
    Object-level permission to only allow owners of an object/admins to view items
    """
    def has_object_permission(self, request, view, obj):
        # Instance must have an attribute named `owner`.
        return (obj.user == request.user) or request.user.is_superuser


class Vw_IsAdminOrReadOnly(permissions.BasePermission):
    """
    Object permission to see the object and only edit if the user is an admin
    """

    def has_permission(self, request, view):
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        if request.method in permissions.SAFE_METHODS:
            return True

        # return if is user is superuser
        return request.user.is_superuser == True

class Vw_IsOwnerOrAdmin(permissions.BasePermission):
    """
    Object permission to see the object and only edit if the user is an admin
    """

    def has_permission(self, request, view):
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        if request.method in permissions.SAFE_METHODS:
            return True

        # return if is user is superuser
        return request.user.is_superuser == True


class Vw_IsAuthenticatedOrCreate(permissions.IsAuthenticated):
    def has_permission(self, request, view):
        if request.method == 'POST':
            return True
        return super(IsAuthenticatedOrCreate, self).has_permission(request, view)
