FROM nixos/nix
RUN echo "experimental-features = nix-command flakes" >> /etc/nix/nix.conf

RUN mkdir /app
WORKDIR /app
ADD . .

RUN nix develop --command bash -c "lein deps"


