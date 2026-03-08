import axios from "axios";

// Base URL of your backend
const API_URL = "http://localhost:8080/auth";

export const register = async (fullName, email, password, role) => {
  try {
    const response = await axios.post(`${API_URL}/register`, {
      fullName,
      email,
      password,
      role,
    });
    return response.data;
  } catch (error) {
    throw error.response.data || { message: "Registration failed" };
  }
};