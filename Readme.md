# SWE645 Assignment 3

## Name: Rudra Chauhan

This project demonstrates containerizing a web application using Docker, pushing it to DockerHub, and preparing it for deployment on Kubernetes. The instructions cover building and running the containerized application locally, and further steps for Kubernetes deployment.

## Project Directory Structure

```
Assignment3
├── swe645-assignment3             # Folder containing web application files
│   ├── src                        # Source code for the Spring Boot application
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com.example.swe645_assignment3  # Java classes and controllers
│   │   │   └── resources          # Configuration and static resources
│   │   │       ├── application.properties  # Spring Boot configuration
│   │   ├── target                 # Compiled application files
│   │   └── pom.xml                # Maven build configuration file
├── Dockerfile                     # Docker configuration for containerizing the web application
├── Jenkinsfile                    # Jenkins configuration for CI/CD pipeline
├── service.yaml                   # Kubernetes Service configuration
├── deployment.yaml                # Kubernetes Deployment configuration
└── README.md                      # Project README file
```

## Spring Boot Implementation

### 1. **Core Components**

#### **a. Controller**

The `StudentSurveyController` manages the application's RESTful APIs. It handles incoming HTTP requests and interacts with the service layer to perform CRUD operations.

Example Controller Code:

```java
@RestController
@RequestMapping("/api/surveys")
public class StudentSurveyController {

    @Autowired
    private StudentSurveyService surveyService;

    @GetMapping
    public List<StudentSurvey> getAllSurveys() {
        return surveyService.getAllSurveys();
    }

    @PostMapping
    public StudentSurvey createSurvey(@RequestBody StudentSurvey survey) {
        return surveyService.saveSurvey(survey);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentSurvey> getSurveyById(@PathVariable Long id) {
        StudentSurvey survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }
}
```

#### **b. Service**

The `StudentSurveyService` encapsulates business logic and interacts with the data access layer (repository). It provides methods for saving, retrieving, and querying surveys.

Example Service Code:

```java
@Service
public class StudentSurveyService {
    @Autowired
    private StudentSurveyRepository surveyRepository;

    public List<StudentSurvey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    public StudentSurvey saveSurvey(StudentSurvey survey) {
        return surveyRepository.save(survey);
    }

    public StudentSurvey getSurveyById(Long id) {
        return surveyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
    }
}
```

#### **c. Repository**

The `StudentSurveyRepository` is an interface that extends Spring Data JPA's `JpaRepository` to provide database operations without needing custom SQL queries.

Example Repository Code:

```java
@Repository
public interface StudentSurveyRepository extends JpaRepository<StudentSurvey, Long> {}
```

### 2. **Entity Class**

The `StudentSurvey` entity represents the survey data stored in the database. It uses JPA annotations to map Java fields to database columns.

Example Entity Code:

```java
@Entity
@Table(name = "student_survey")
public class StudentSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String telephone;
    private String email;
    private String surveyDate;
    private String likedMost;
    private String interestSource;
    private String recommendation;
    // Getters and Setters
}
```

### 3. **Database Integration**

#### Step 1: Create AWS RDS MySQL Database

#### Prerequisites

- AWS Account with access to Amazon RDS
- AWS Management Console access
- Basic understanding of AWS networking and security groups

#### Step-by-Step RDS MySQL Database Creation

1. **Login to AWS Management Console**

   - Navigate to the Amazon RDS service
   - Click on "Create database"

2. **Choose Database Creation Method**

   - Select "Standard create"
   - Choose "MySQL" as the engine type

3. **Configure Database Settings**

   - Template: Choose "Free tier" or appropriate tier
   - DB instance identifier: `swe645-mysql-db`
   - Master username: `admin` (or your preferred username)
   - Master password: Create a strong, unique password

4. **Instance Configuration**

   - DB instance class: `db.t3.micro` (free tier eligible)
   - Storage type: General Purpose SSD (gp2)
   - Allocated storage: Minimum 20 GiB

5. **Connectivity**

   - Virtual Private Cloud (VPC): Default VPC or create a new one
   - Public accessibility: Yes (to allow external connections)

6. **Additional Configuration**

   - Initial database name: `swe645db`
   - Backup: Enable automatic backups (optional)
   - Maintenance window: Choose a suitable time

7. **Create Database**
   - Review settings
   - Click "Create database"

#### Connecting to the Database

1. **Retrieve Connection Endpoint**
   - Go to RDS Dashboard
   - Click on your database instance
   - Copy the "Endpoint" value

The application uses a MySQL database hosted on AWS RDS. Spring Boot's `application.properties` file configures the database connection.

**application.properties**:

```properties
spring.datasource.url=jdbc:mysql://<rds-endpoint>:3306/<rds-dbname>
spring.datasource.username=<db-username>
spring.datasource.password=<db-password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Replace `<rds-endpoint>`, `<db-username>`, and `<db-password>` with actual values.

## Build JAR File with Maven

### Prerequisites

- Apache Maven installed and added to the system PATH.
- Java Development Kit (JDK) installed and configured with `JAVA_HOME`.

### Maven Configuration (`pom.xml`)

The `pom.xml` file in the project root is used to build the Spring Boot application into a JAR file. Make sure Maven is configured in your environment.

### Building the JAR File

Run the following commands in the project directory where `pom.xml` is located:

```bash
# Clean and package the application
mvn clean package -DskipTests
```

The resulting JAR file will be located at:

```
swe645-assignment3/target/swe645-assignment3-0.0.1-SNAPSHOT.jar
```

## Step 1: Containerizing JAVA Application

### Prerequisites

- Docker Desktop installed and running
- Basic understanding of containerization concepts

### Dockerfile

The Dockerfile defines the instructions to build a container image for the web application using the Nginx server.

```dockerfile
# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY ./swe645-assignment3/target/swe645-assignment3-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Building and Running the Container

To build and run the container locally, use the following commands:

```bash
# Build the Docker image
docker build -t <container-name> .

# Run the container on host port 8090
docker run -p 8090:80 <container-name>

# Verify running containers
docker ps
```

- `docker build -t <container-name> .` - Builds the Docker image from the Dockerfile.
- `docker run -p 8090:80 <container-name>` - Runs the container, mapping host port `8090` to container port `80`.
- `docker ps` - Lists running containers.

### Access the Application

Once running, the web application will be accessible at `http://localhost:8090`. Verify that your website loads correctly and all functionality works as expected.

## Step 2: Push the Container to DockerHub

### Prerequisites

- DockerHub account
- Docker CLI logged in to your DockerHub account

To make the container accessible to Kubernetes or other deployment platforms, push it to DockerHub:

1. Log in to DockerHub:

```bash
docker login
```

2. Tag the container with your DockerHub repository name:

```bash
docker tag <container-name> <your-dockerhub-username>/<container-name>
```

3. Push the container to DockerHub:

```bash
docker push <your-dockerhub-username>/<container-name>
```

Verify that your image appears in your DockerHub repository before proceeding to the next step.

## Step 3: AWS EKS Setup and Deployment

### Prerequisites

- AWS Account (or AWS Learner Lab access)
- AWS CLI installed
- kubectl installed

### Creating EKS Cluster

1. Navigate to AWS Management Console:

   - Search for "EKS" or find it under Services
   - Ensure you're in your desired region (e.g., us-east-1)

2. Create EKS Cluster:

   - Click "Create cluster"
   - Provide a cluster name
   - Select Kubernetes version (latest stable recommended)
   - Use default IAM LabRole
   - Keep default VPC and subnet configurations
   - For demo purposes, you can use default security group settings

3. Create Node Group:
   - After cluster creation, navigate to the "Compute" tab
   - Click "Add Node Group"
   - Configure node group settings:
     - Name your node group
     - Choose instance type (t3.medium recommended for testing)
     - Set scaling configuration (e.g Min: 1, Max: 1)
     - Review and create

Note: Cluster creation takes approximately 15-20 minutes. Node group creation takes an additional 5-10 minutes.

### Connecting to Kubernetes Cluster

1. Configure AWS CLI:

   ```bash
   aws configure
   ```

   When prompted, enter:

   - AWS Access Key ID
   - AWS Secret Access Key
   - Region (e.g., us-east-1)
   - Output format (json)

   Note: Access key, secret key, and session token can be found in AWS Learner Lab's "AWS Details" section:

   - Click "AWS Details"
   - Copy Access key, Secret key, and Session token

   For AWS Learner Lab users:

   ```bash
   # Set session token (required for Learner Lab)
   aws configure set aws_session_token <session-token>
   ```

2. Update Kubernetes Configuration:

   ```bash
   aws eks update-kubeconfig --region us-east-1 --name <cluster-name>
   ```

   This updates your `.kube/config` file (typically located at `C:/Users/<current-username>/.kube`)

3. Verify Connection:
   ```bash
   kubectl get nodes
   ```
   Should display your node(s) in "Ready" state.

### Kubernetes Deployment Configuration

1. Create `deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: swe645-assignment3-deployment
  labels:
    app: swe645-assignment3
spec:
  replicas: 1
  selector:
    matchLabels:
      app: swe645-assignment3
  template:
    metadata:
      labels:
        app: swe645-assignment3
    spec:
      containers:
        - name: swe645-assignment3-container
          image: rudra2807/swe645-assignment3:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_DATASOURCE_URL
              value: jdbc:mysql://swe645-db1-instance.c4jwqm8i3jbz.us-east-1.rds.amazonaws.com:3306/swe645_db1
            - name: SPRING_DATASOURCE_USERNAME
              value: admin
            - name: SPRING_DATASOURCE_PASSWORD
              value: swe645admin
```

2. Create `service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: swe645-assignment3-service
spec:
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  selector:
    app: swe645-assignment3
```

3. Apply and Verify Deployment:

```bash
# Apply configurations
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Verify deployment status
# View deployments
kubectl get deployments

# View pods
kubectl get pods

# View services (includes LoadBalancer external IP)
kubectl get services

# View detailed information
kubectl describe deployment my-website
kubectl describe service my-website-service

# View pod logs
kubectl logs <pod-name>
```

The LoadBalancer external IP can take a few minutes to provision. Once available, your website will be accessible through this IP.

## Step 4: CI/CD Pipeline with Jenkins

### Prerequisites

- Jenkins installed and running
- GitHub repository with your project
- Basic understanding of CI/CD concepts

### Jenkins Setup

1. Install Jenkins:

   - Download and install Jenkins from official website
   - Install required plugins:
     - Git plugin
     - Pipeline plugin
     - Docker Pipeline plugin
     - Kubernetes Continuous Deploy plugin

2. Configure Jenkins Credentials:

   - Navigate to "Manage Jenkins" → "Manage Credentials"
   - Add credentials for:
     - GitHub
     - DockerHub
     - AWS (if using programmatic deployment)
   - Note: Keep the domain "Global"

3. Add Maven in Jenkins:

   - Install Maven on Jenkins:
     - Go to Manage Jenkins > Global Tool Configuration.
     - Under the Maven section, click Add Maven. Provide a name for the Maven installation, such as Maven.
     - Check the box for Install automatically and select the desired Maven version.
     - Save the configuration.

4. Create Pipeline:

   - Click "New Item"
   - Choose "Pipeline"
   - Configure:
     - Enable "GitHub hook trigger for GITScm polling"
     - Under Pipeline Configuration:
       - Definition: "Pipeline script from SCM"
       - SCM: Git
       - Repository URL: Your GitHub repo URL
       - Credentials: Select GitHub credentials
       - Branch Specifier: \*/master (or your main branch)
       - Script Path: Jenkinsfile

5. Create `Jenkinsfile` in your repository:

```groovy
pipeline {
    agent any
    environment {
        DOCKER_IMAGE = ''  // Replace with your Docker image name
        DOCKER_TAG = 'latest'
        KUBECONFIG_PATH = ''  // Replace with your kubeconfig path
    }
    stages {
        stage('Clone Repository') {
            steps {
                // Clone the repository
                git branch: 'main',
                    credentialsId: '',  // Add your GitHub credentials ID
                    url: ''  // Add your repository URL
            }
        }
        stage('Build JAR File') {
            steps {
                script {
                    dir('directory where pom.xml is present') {
                        def mvnHome = tool 'Maven' // Use Maven installed in Jenkins
                        bat "${mvnHome}/bin/mvn clean package -DskipTests"
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                }
            }
        }
        stage('Push to DockerHub') {
            steps {
                script {
                    withCredentials([
                        usernamePassword(credentialsId: '',  // Add your DockerHub credentials ID
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASS')
                    ]) {
                        bat "echo logging into Docker Hub..."
                        bat "echo | set /p=\"%DOCKER_PASS%\" | docker login --username %DOCKER_USERNAME% --password-stdin"
                        bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    }
                }
            }
        }
        stage('Deploy to EKS') {
            steps {
                // Using the Kubeconfig to update the Kubernetes deployment
                bat "kubectl set image deployment/my-website my-website=${DOCKER_IMAGE}:${DOCKER_TAG} --kubeconfig=\"C:\\Program Files\\Jenkins\\kubeconfig\""
                // Restart the pods to reflect the new changes
                bat "kubectl rollout restart deployment my-website --kubeconfig=\"C:\\Program Files\\Jenkins\\kubeconfig\""
            }
        }
    }
}
```

#### Special Configuration for AWS Learner Lab Users

When using AWS Learner Lab, you need to manually modify the kubeconfig file to include your AWS credentials. The file is typically located at:

- Windows: `%UserProfile%\.kube\config`
- Linux/Mac: `~/.kube/config`

1. Update the kubeconfig file with the following structure:

```yaml
apiVersion: v1
clusters:
  - cluster:
      certificate-authority-data: ""
      server: https://EA4BAFE2AB12E04FBC530AD382793BF1.gr7.us-east-1.eks.amazonaws.com
    name: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
contexts:
  - context:
      cluster: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
      user: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
    name: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
current-context: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
kind: Config
preferences: {}
users:
  - name: arn:aws:eks:us-east-1:943197202758:cluster/swe645Cluster
    user:
      exec:
        apiVersion: client.authentication.k8s.io/v1beta1
        args:
          - --region
          - us-east-1
          - eks
          - get-token
          - --cluster-name
          - swe645Cluster
        command: aws
        env:
          - name: AWS_ACCESS_KEY_ID
            value: "" # Add your AWS access key here
          - name: AWS_SECRET_ACCESS_KEY
            value: "" # Add your AWS secret key here
          - name: AWS_SESSION_TOKEN
            value: "" # Add your AWS session token here
```

Important Notes:

1. The `env` section under `users` is crucial for AWS Learner Lab authentication
2. You must update these credentials every time you:
   - Start a new Learner Lab session
   - Resume a suspended session
   - After session timeout

To update credentials:

1. Go to AWS Learner Lab dashboard
2. Click "AWS Details"
3. Copy the new:
   - AWS_ACCESS_KEY_ID
   - AWS_SECRET_ACCESS_KEY
   - AWS_SESSION_TOKEN
4. Update these values in your kubeconfig file

Important Notes for Jenkinsfile:

1. Replace `DOCKER_IMAGE` with your Docker image name
2. Add your GitHub `credentialsId` in the Clone Repository stage
3. Add your repository URL in the Clone Repository stage
4. Add your DockerHub `credentialsId` in the Push to DockerHub stage
5. Ensure the kubeconfig path matches your Jenkins server configuration

6. ### Setting Up GitHub Webhook with ngrok

To enable automatic build triggers when you push to GitHub, you need to configure a webhook. Since Jenkins is running locally, we'll use ngrok to create a public endpoint.

1. Install ngrok:

   - Download from [ngrok website](https://ngrok.com/)
   - Extract the executable
   - Add to system PATH (optional)

2. Create Public Endpoint:

   ```bash
   # Replace <jenkins-port> with your Jenkins port (typically 8080)
   ngrok http <jenkins-port>
   ```

   ngrok will display a forwarding URL like:

   ```
   Forwarding  https://abc123.ngrok.io -> localhost:8080
   ```

3. Configure GitHub Webhook:
   - Go to your GitHub repository
   - Navigate to Settings → Webhooks
   - Click "Add webhook"
   - Configure webhook:
     - Payload URL: `https://abc123.ngrok.io/github-webhook/`
     - Content type: application/json
     - Which events?: Select "Just the push event"
     - Active: Check the box
   - Click "Add webhook"

### Automated Deployment Process

1. Developer pushes code to GitHub
2. GitHub webhook triggers Jenkins pipeline
3. Jenkins:
   - Builds new Docker image
   - Pushes to DockerHub
   - Updates Kubernetes deployment
4. Kubernetes:
   - Pulls new image
   - Updates pods with zero-downtime deployment
   - Routes traffic through LoadBalancer
