package com.lrc.missionO2.entity;

/**

The "UserRole" enum represents the role or privileges assigned to a user in the system.
It has two predefined roles: ROLE_USER and ROLE_ADMIN.
ROLE_USER: Represents a regular user role with basic privileges.
ROLE_ADMIN: Represents an administrator role with elevated privileges and administrative access.
The user role is used for authentication and authorization purposes in the system.
By assigning different roles to users, the system can control their access to various resources and functionalities.
*/
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;
}
