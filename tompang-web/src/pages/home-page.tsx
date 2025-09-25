import { useQuery } from "@tanstack/react-query"
import { test } from "../api/services/user/user.service"

export default function HomePage() {

  const {data, isError, error} = useQuery({
    queryKey: ["buh"],
    queryFn: () => test()
  })
  
  return (
    <div>
      Welcome to Tompang Carpool!
      {isError && error.message}
    </div>
  )
}