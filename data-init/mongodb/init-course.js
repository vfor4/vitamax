// Pre-generate course UUIDs to reuse in recommendations
const courseIds = {
  java: "fab16e17-63c3-4248-9731-248b58592b60",
  spring: "fab16e17-63c3-4248-9731-248b58592b61",
  microservices: "fab16e17-63c3-4248-9731-248b58592b63",
  mongodb: "fab16e17-63c3-4248-9731-248b58592b64",
  docker: "fab16e17-63c3-4248-9731-248b58592b65",
  rest: "fab16e17-63c3-4248-9731-248b58592b66"
};

db.course.insertMany([
  { courseId: courseIds.java, name: "Java Fundamentals" },
  { courseId: courseIds.spring, name: "Spring Boot Essentials" },
  { courseId: courseIds.microservices, name: "Microservices Architecture" },
  { courseId: courseIds.mongodb, name: "MongoDB for Developers" },
  { courseId: courseIds.docker, name: "Docker & Kubernetes" },
  { courseId: courseIds.rest, name: "REST API Design" }
]);

db.recommendation.insertMany([
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b60",
    courseId: courseIds.java,
    author: "Alice",
    rate: 5,
    content: "Excellent introduction to Java."
  },
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b61",
    courseId: courseIds.spring,
    author: "Bob",
    rate: 5,
    content: "Very practical Spring Boot course."
  },
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b62",
    courseId: courseIds.microservices,
    author: "Charlie",
    rate: 4,
    content: "Great overview of microservices concepts."
  },
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b63",
    courseId: courseIds.mongodb,
    author: "David",
    rate: 5,
    content: "MongoDB explained clearly with examples."
  },
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b64",
    courseId: courseIds.docker,
    author: "Emma",
    rate: 4,
    content: "Good hands-on guide for Docker and Kubernetes."
  },
  {
    recommendationId: "rab16e17-63c3-4248-9731-248b58592b65",
    courseId: courseIds.rest,
    author: "Frank",
    rate: 5,
    content: "Clear and well-structured REST API design."
  }
]);