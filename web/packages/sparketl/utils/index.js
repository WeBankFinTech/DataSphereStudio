export const toJson = (s, separator) => {
  let json = {};
  s.split(separator).forEach((line) => {
    let option = line.split("=").map((item) => item.trim());
    if (option[0] !== "") json[option[0]] = option[1] || "";
  });
  return json;
};
