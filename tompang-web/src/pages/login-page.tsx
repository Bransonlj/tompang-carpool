import { useState } from "react";
import { useAuth } from "../context/auth-context";
import { Link } from "react-router";

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    login({email, password});
  };

  return (
    <div className="flex items-center justify-center h-screen">
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow-lg rounded-xl p-8 w-80"
      >
        <h2 className="text-xl font-bold mb-4">Login</h2>
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
          className="w-full bg-blue-500 text-white px-4 py-2 rounded-lg"
        >
          Login
        </button>
        <p className="mt-4 text-sm text-gray-600">
          Don’t have an account?{" "}
          <Link to="/auth/register" className="text-blue-500 underline">
            Register
          </Link>
        </p>
      </form>
    </div>
  );
}