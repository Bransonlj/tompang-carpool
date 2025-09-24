import { useState } from "react";
import { useAuth } from "../context/auth-context";
import { Link } from "react-router";

export default function RegisterPage() {
  const { register } = useAuth();
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [firstName, setFirstName] = useState<string>("");
  const [lastName, setLastName] = useState<string>("");

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    register({
      email, password, firstName, lastName
    });
  };

  return (
    <div className="flex items-center justify-center h-screen">
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow-lg rounded-xl p-8 w-80"
      >
        <h2 className="text-xl font-bold mb-4">Register</h2>
        <input
          type="text"
          placeholder="First Name"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          className="w-full px-3 py-2 border rounded-lg mb-4"
        />
        <input
          type="text"
          placeholder="Last Name"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          className="w-full px-3 py-2 border rounded-lg mb-4"
        />
        <input
          type="text"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full px-3 py-2 border rounded-lg mb-4"
        />
        <input
          type="text"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full px-3 py-2 border rounded-lg mb-4"
        />
        <button
          type="submit"
          className="w-full bg-green-500 text-white px-4 py-2 rounded-lg"
        >
          Register
        </button>
        <p className="mt-4 text-sm text-gray-600">
          Already have an account?{" "}
          <Link to="/auth/login" className="text-blue-500 underline">
            Login
          </Link>
        </p>
      </form>
    </div>
  );
}