import Avatar, { type AvatarProps } from "@mui/material/Avatar";

function stringToColor(string: string) {
  let hash = 0;
  let i;

  /* eslint-disable no-bitwise */
  for (i = 0; i < string.length; i += 1) {
    hash = string.charCodeAt(i) + ((hash << 5) - hash);
  }

  let color = '#';

  for (i = 0; i < 3; i += 1) {
    const value = (hash >> (i * 8)) & 0xff;
    color += `00${value.toString(16)}`.slice(-2);
  }
  /* eslint-enable no-bitwise */

  return color;
}

interface UserAvatarProps extends AvatarProps {
  name: string;
  src: string;
}

export default function UserAvatar({
  name,
  src,
  className,
  sx,
  ...props
}: UserAvatarProps) {
  return (
    <Avatar 
      className={`${className}`}
      sx={{
        bgcolor: stringToColor(name),
        ...sx,
      }}
      alt={name}
      src={src}
      {...props}
    />
  );
}