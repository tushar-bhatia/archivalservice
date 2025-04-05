# 🌍 Data Archival Service

## Overview

The **Data Archival Service** is a robust and scalable platform designed to archive relational database data into geographically distributed archival storage systems. The system supports policy-driven data movement and role-based access control to ensure secure, compliant, and efficient long-term data retention.

## ✨ Features

- 🌐 **Geo-Distributed Archival**  
  Archives data from source relational databases to storage located in various global regions.

- 📅 **Policy-Based Archival and Deletion**  
  Admins can define **archival policies** for each table specifying:
    - When data should be archived
    - When data should be permanently deleted from the archive

- 🔐 **Role-Based Access Control**
    - **Admin Privileges**:
        - Create, update, delete, and view user accounts
        - Assign or revoke roles for users
        - View roles assigned to any user
        - Configure table-specific archival and deletion policies
        - View all archived table data
    - **User Access**:
        - Restricted to viewing data of only those tables for which they have explicit access

- ⚙️ **Cross-Platform Compatibility**
    - Fully deployable in Linux environments for seamless integration in various infrastructure setups

## 🛡️ Security

- Only **ADMIN** users can:
    - Manage users and roles
    - Configure or change critical archival and data cleanup settings
- Data visibility is strictly enforced via user-role permissions

## 🚀 Deployment

- Designed to run on **Linux** for production-grade stability and platform compatibility
- Can be integrated into microservices-based systems

## 📚 Usage

### Admin Operations:
-  Create a new user
-  Update user details
-  Delete a user
-  Retrieve all user details
-  Assign role to a user
- Revoke role from a user
- View roles assigned to a user
- Configure archival and deletion policies

### User Operations:
-  View data of permitted tables only

## 📦 Tech Stack

- Java with Spring Boot
- Relational Databases (MySQL)
- Secure REST APIs
- Role-based access control (RBAC)
- Deployable on Linux servers

## ✅ Future Enhancements

- UI dashboard for policy and user management
- Audit logging and compliance reporting
- Integration with cloud-based cold storage solutions