db.course.insertMany([
  { courseId: 1, name: "Java Fundamentals" },
  { courseId: 2, name: "Spring Boot Essentials" },
  { courseId: 3, name: "Microservices Architecture" },
  { courseId: 4, name: "MongoDB for Developers" },
  { courseId: 5, name: "Docker & Kubernetes" },
  { courseId: 6, name: "REST API Design" },
]);
db.recommendation.insertMany([
  {
    courseId: 1,
    recommendationId: 1,
    author: "Alice",
    rate: 5,
    content: "Excellent introduction to Java."
  },
  {
    courseId: 2,
    recommendationId: 2,
    author: "Bob",
    rate: 5,
    content: "Very practical Spring Boot course."
  },
  {
    courseId: 3,
    recommendationId: 3,
    author: "Charlie",
    rate: 4,
    content: "Great overview of microservices concepts."
  },
  {
    courseId: 4,
    recommendationId: 4,
    author: "David",
    rate: 5,
    content: "MongoDB explained clearly with examples."
  },
  {
    courseId: 5,
    recommendationId: 5,
    author: "Emma",
    rate: 4,
    content: "Good hands-on guide for Docker and Kubernetes."
  },
  {
    courseId: 6,
    recommendationId: 6,
    author: "Frank",
    rate: 5,
    content: "Clear and well-structured REST API design."
  },
]);
