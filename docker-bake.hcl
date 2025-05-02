group "default" {
    targets = ["filesystem"]
}

target "filesystem" {
    context = "./filesystem"
    dockerfile = "./Dockerfile"
    tags = ["ravidgontov/fs-test:latest"]
    args = {
      "FOLDER" = "filesystem"
    }
}
