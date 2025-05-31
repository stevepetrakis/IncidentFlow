# Emergency Response Management System

A web-based platform for managing emergency incidents with multiple user roles and real-time communication features.

## Features

### User Roles

1. **Guest Users**
   - Submit incidents
   - View incidents
   - Access AI help feature

2. **Registered Users**
   - Edit personal information
   - Submit incidents
   - View incidents
   - View incident history
   - Access chat rooms
   - AI assistance
   - Receive notifications for nearby incidents

3. **Volunteers**
   - Edit profile information
   - Submit incidents
   - View incidents
   - Participate incidents
   - View participations
   - Access chat rooms
   - Receive notifications for nearby incidents

4. **Administrators**
   - Submit incidents
   - Edit User/Volunteer profile information
   - View incidents
   - Update incident status
   - Manage users and participants
   - Handle volunteer requests
   - Access chat rooms
   - View analytics and charts

## Technical Stack

- **Frontend:** HTML, JavaScript, CSS, Bootstrap
- **Backend:** Java EE
- **Database Integration:** MySQL
- **API Integration:** 
  - Leaflet for maps
  - Chart.js for data visualization
  - Google Charts

## Dependencies

- Java EE Web API 7.0
- JUnit 4.12
- Google Gson 2.11.0
- MySQL Connector Java 8.0.25
- JSON Library 20230618

## External Resources

The system integrates with several external services for fire monitoring:

- European Forest Fire Information System
- NASA Live Fires in Greece
- Interreg Live Fires in Greece

## Getting Started

1. Build the project using Maven:
   ```bash
   mvn clean install
   ```

2. Deploy the generated WAR file (`target/finalproject-1.0-SNAPSHOT.war`) to your Java EE server

3. Access the application through the login menu:
   - Navigate to `index.html` for new user registration
   - Use `login_menu.html` to access different user roles

## Security Features

- Role-based access control
- Secure login system
- Session management
- Protected admin functionalities

## Communication Features

- Real-time chat rooms
- Volunteer notification system
- Incident status updates
- User participation tracking

## Data Visualization

- Interactive charts and graphs
- Map-based incident visualization
- Historical data analysis
- User activity monitoring
